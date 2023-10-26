package com.elemengine.elemengine.ability;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.Event;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityInstance.Phase;
import com.elemengine.elemengine.ability.activation.SequenceInfo;
import com.elemengine.elemengine.ability.activation.Trigger;
import com.elemengine.elemengine.ability.attribute.Attribute;
import com.elemengine.elemengine.ability.type.Bindable;
import com.elemengine.elemengine.ability.type.Passive;
import com.elemengine.elemengine.ability.type.combo.Combo;
import com.elemengine.elemengine.ability.type.combo.ComboTree;
import com.elemengine.elemengine.ability.type.combo.ComboValidator;
import com.elemengine.elemengine.event.ability.InstanceStartEvent;
import com.elemengine.elemengine.event.ability.InstanceStopEvent;
import com.elemengine.elemengine.event.ability.InstanceStopEvent.Reason;
import com.elemengine.elemengine.event.user.UserInputTriggerEvent;
import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.storage.Config;
import com.elemengine.elemengine.util.Events;
import com.elemengine.elemengine.util.reflect.DynamicLoader;
import com.google.common.base.Preconditions;

public class Abilities extends Manager {

    private Map<Class<? extends AbilityInfo>, AbilityInfo> cache = new HashMap<>();
    private Map<String, AbilityInfo> combos = new HashMap<>();
    private ComboTree root = new ComboTree();

    private long prevTick = System.currentTimeMillis();
    private Set<AbilityInstance> active = new HashSet<>();

    @Override
    protected int priority() {
        return 30;
    }

    @Override
    protected boolean active() {
        return true;
    }

    @Override
    protected void startup() {
        DynamicLoader.loadDir(Elemengine.plugin(), Elemengine.getAddonsFolder(), true, (c) -> AbilityInfo.class.isAssignableFrom(c) || AbilityInstance.class.isAssignableFrom(c), (clazz) -> {
            if (AbilityInfo.class.isAssignableFrom(clazz)) {
                try {
                    register((AbilityInfo) clazz.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (AbilityInstance.class.isAssignableFrom(clazz)) {
                Map<String, Field> attributes = new HashMap<>();

                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Attribute.class)) {
                        attributes.put(field.getAnnotation(Attribute.class).value().toLowerCase(), field);
                    }
                }

                AbilityInstance.ATTRIBUTES.put(clazz.asSubclass(AbilityInstance.class), attributes);
            }
        });
    }

    @Override
    protected void tick() {
        Iterator<AbilityInstance> iter = active.iterator();
        double deltaTime = (System.currentTimeMillis() - prevTick) / 1000D;

        while (iter.hasNext()) {
            AbilityInstance inst = iter.next();

            switch (inst.getPhase()) {
                case UPDATING:
                    if (inst.update(deltaTime)) {
                        break;
                    }
                    inst.stop();
                case STOPPING:
                    iter.remove();
                case STARTING:
            }
        }

        prevTick = System.currentTimeMillis();
    }

    @Override
    protected void clean() {
        cache.clear();
        active.clear();
    }

    public <T extends AbilityInfo> void register(T ability) {
        Preconditions.checkArgument(ability != null, "Cannot register null ability");
        Preconditions.checkArgument(!cache.values().stream().anyMatch(a -> a.getName().equalsIgnoreCase(ability.getName())), "Attmempted to load an ability with existing name: " + ability.getName());

        cache.put(ability.getClass(), Config.process(ability));

        if (ability instanceof Combo) {
            try {
                combos.put(SequenceInfo.stringify(root.build(((Combo) ability).getSequence())), ability);
            } catch (Exception e) {
                System.out.println(ability.getName() + " attempted to register a combo sequence which would never be activated!");
            }
        }
        
        if (!assignAbilityID(ability)) {
            Elemengine.database().send("INSERT INTO t_ability_ids (ability_name) VALUES ('" + ability.getName().toLowerCase() + "')").thenRun(() -> {
                assignAbilityID(ability);
            }).join();
        }
        
        ability.onRegister();
        Events.register(ability, Elemengine.plugin());
    }
    
    private boolean assignAbilityID(AbilityInfo info) {
        ResultSet rs = Elemengine.database().read("SELECT id FROM t_ability_ids WHERE ability_name = '" + info.getName().toLowerCase() + "'");
        try {
            if (rs.next()) {
                info.bitFlag = BigInteger.TWO.pow(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public <T extends AbilityInfo> Optional<T> getInfo(Class<T> clazz) {
        if (clazz == null) return null;
        if (cache.get(clazz) == null) return null;
        return Optional.of(clazz.cast(cache.get(clazz)));
    }

    public Optional<AbilityInfo> getInfo(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return cache.values().stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Set<AbilityInfo> fromSkill(Skill skill) {
        return cache.values().stream().filter(a -> a.getSkill() == skill).collect(Collectors.toSet());
    }

    public Set<AbilityInfo> getUserBindables(AbilityUser user) {
        return cache.values().stream().filter(user::canBind).collect(Collectors.toSet());
    }

    public boolean activate(AbilityUser user, Trigger trigger, Event provider) {
        if (user == null || trigger == null) {
            return false;
        } else if (Events.call(new UserInputTriggerEvent(user, trigger, provider)).isCancelled()) {
            return false;
        }

        AbilityInfo ability = user.getBoundAbility().orElseGet(() -> null);

        if (ability == null) {
            return false;
        }
        
        AbilityInstance instance = null;

        if (trigger.canCombo()) {
            ComboValidator combo = user.updateCombos(ability, trigger, root);
            if (combo != null) {
                ability = combos.get(SequenceInfo.stringify(combo.getSequence()));
                instance = ((Combo) ability).createComboInstance(user);
            }
        } else if (ability.canActivate(user, trigger)) {
            instance = ((Bindable) ability).createBindInstance(user, trigger, provider);
        }

        return this.startInstance(instance);
    }

    public boolean startInstance(AbilityInstance instance) {
        if (instance == null || instance.getUser() == null) {
            return false;
        } else if (active.contains(instance)) {
            return false;
        } else if (Events.call(new InstanceStartEvent(instance)).isCancelled()) {
            return false;
        }
        
        instance.start();

        if (instance.getPhase() == Phase.STOPPING) {
            return false;
        }

        if (instance.hasUpdate()) {
            active.add(instance);
        }

        return true;
    }

    public void stopInstance(AbilityInstance instance) {
        if (instance == null) {
            return;
        }

        this.stop(instance, Reason.FORCED);
    }

    private void stop(AbilityInstance instance, Reason reason) {
        Events.call(new InstanceStopEvent(instance, reason));
        instance.stop();
    }

    public void refresh(AbilityUser user) {
        user.stopInstances();

        for (Skill skill : user.getSkills()) {
            for (AbilityInfo ability : Abilities.manager().fromSkill(skill)) {
                if (ability instanceof Passive passive) {
                    this.startInstance(passive.createPassiveInstance(user));
                }
            }
        }
    }

    public Set<AbilityInfo> registered() {
        return new HashSet<>(cache.values());
    }

    public static Abilities manager() {
        return Manager.of(Abilities.class);
    }
}
