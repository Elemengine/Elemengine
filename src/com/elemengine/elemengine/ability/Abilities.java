package com.elemengine.elemengine.ability;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityInstance.Phase;
import com.elemengine.elemengine.ability.activation.Trigger;
import com.elemengine.elemengine.ability.attribute.Attribute;
import com.elemengine.elemengine.ability.type.Bindable;
import com.elemengine.elemengine.ability.type.Passive;
import com.elemengine.elemengine.ability.type.combo.Combo;
import com.elemengine.elemengine.ability.type.combo.ComboTree;
import com.elemengine.elemengine.ability.type.combo.ComboValidator;
import com.elemengine.elemengine.ability.type.combo.SequenceInfo;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.event.ability.InstanceStartEvent;
import com.elemengine.elemengine.event.ability.InstanceStopEvent;
import com.elemengine.elemengine.event.ability.InstanceStopEvent.Reason;
import com.elemengine.elemengine.event.element.ElementChangeEvent;
import com.elemengine.elemengine.event.user.UserInputTriggerEvent;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.reflect.Dynamics;
import com.elemengine.elemengine.util.spigot.Events;
import com.elemengine.elemengine.util.spigot.Threads;
import com.google.common.base.Preconditions;

public class Abilities extends Manager implements Listener {

    private final Map<Class<? extends AbilityInfo>, AbilityInfo> cache = new HashMap<>();
    private final Map<String, AbilityInfo> combos = new HashMap<>();
    private final ComboTree root = new ComboTree();

    private long prevTick = System.currentTimeMillis();
    private final Set<AbilityInstance> active = new HashSet<>();
    
    @Override
    protected int priority() {
        return 30;
    }

    @Override
    protected boolean active() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void startup() {
        Dynamics.loadDir(Elemengine.getAbilitiesFolder(), Elemengine.class.getClassLoader(), true, (c) -> AbilityInfo.class.isAssignableFrom(c) || AbilityInstance.class.isAssignableFrom(c), (clazz) -> {
            if (AbilityInfo.class.isAssignableFrom(clazz)) {
                try {
                    Constructor<?> cons = clazz.getDeclaredConstructor();
                    cons.setAccessible(true);
                    register((AbilityInfo) cons.newInstance());
                } catch (Exception e) {
                    Elemengine.plugin().getLogger().warning("Unable to construct registration instance for " + clazz + ". AbilityInfo subclasses are expected to have a default constructor (one with no parameters).");
                }
            } else if (AbilityInstance.class.isAssignableFrom(clazz)) {
                Map<String, Field> attributes = new HashMap<>();

                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Attribute.class)) {
                        Attribute attr = field.getAnnotation(Attribute.class);
                        
                        attributes.put(attr.value().isBlank() ? field.getName() : attr.value().toLowerCase(), field);
                    }
                }

                AbilityInstance.ATTRIBUTE_FIELDS.put((Class<? extends AbilityInstance>) clazz, attributes);
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
        Preconditions.checkArgument(cache.values().stream().noneMatch(a -> a.getName().equalsIgnoreCase(ability.getName())), "Attmempted to load an ability with existing name: " + ability.getName());

        cache.put(ability.getClass(), Config.process(ability));

        if (ability instanceof Combo) {
            try {
                combos.put(SequenceInfo.stringify(root.build(((Combo) ability).getSequence())), ability);
            } catch (Exception e) {
                System.out.println(ability.getName() + " attempted to register a combo sequence which would never be activated!");
            }
        }
        
        /*
        if (!assignAbilityID(ability)) {
            Elemengine.database().send("INSERT INTO t_ability_ids (ability_name) VALUES ('" + ability.getName().toLowerCase() + "')").thenRun(() -> {
                assignAbilityID(ability);
            }).join();
        }
        */
        
        ability.onRegister();
        Events.register(ability);
        Elemengine.plugin().getLogger().info("Ability registered - " + ability.getName());
    }
    
    /*
    private boolean assignAbilityID(AbilityInfo info) {
        final Box<Boolean> value = Box.of(false);
        
        Elemengine.database().read("SELECT id FROM t_ability_ids WHERE ability_name = '" + info.getName().toLowerCase() + "'", rs -> {
            if (rs.next()) {
                info.bitFlag = BigInteger.TWO.pow(rs.getInt(1));
                value.set(true);
            }
        });
        
        return value.get();
    }
    */

    public <T extends AbilityInfo> Optional<T> getInfo(Class<T> clazz) {
        if (clazz == null || cache.get(clazz) == null) return Optional.empty();
        return Optional.of(clazz.cast(cache.get(clazz)));
    }

    public Optional<AbilityInfo> getInfo(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return cache.values().stream().filter(a -> a.getName().equalsIgnoreCase(name.replace("_", " "))).findFirst();
    }

    public Set<AbilityInfo> fromElement(Element element) {
        return cache.values().stream().filter(a -> a.isForElement(element)).collect(Collectors.toSet());
    }

    public Set<AbilityInfo> getUserBindables(AbilityUser user) {
        return cache.values().stream().filter(user::canBind).collect(Collectors.toSet());
    }

    public boolean activate(AbilityUser user, Trigger trigger, Event provider) {
        if (user == null || trigger == null || Events.call(new UserInputTriggerEvent(user, trigger, provider)).isCancelled()) {
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
        }
        
        if (instance == null && ability.canActivate(user, trigger)) {
            instance = ((Bindable) ability).createBindInstance(user, trigger, provider);
        }

        return this.startInstance(instance);
    }

    public boolean startInstance(AbilityInstance instance) {
        if (instance == null || instance.getUser() == null || active.contains(instance)) {
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

        for (Element element : user.getElements()) {
            for (AbilityInfo ability : Abilities.manager().fromElement(element)) {
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
    
    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        AbilityUser user = Users.manager().get(event.getPlayer()).get();

        if (user == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.useInteractedBlock() != Event.Result.DENY)) {
            this.activate(user, Trigger.RIGHT_CLICK_BLOCK, event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerSwing(PlayerAnimationEvent event) {
        AbilityUser user = Users.manager().get(event.getPlayer()).get();

        if (user == null || event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }

        this.activate(user, Trigger.LEFT_CLICK, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteractEntity(PlayerInteractAtEntityEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer()).get();

        if (user == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.activate(user, Trigger.RIGHT_CLICK_ENTITY, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSneak(PlayerToggleSneakEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer()).get();

        if (user == null) {
            return;
        }

        this.activate(user, event.isSneaking() ? Trigger.SNEAK_DOWN : Trigger.SNEAK_UP, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getEntity().getUniqueId()).get();

        if (user == null) {
            return;
        }

        this.activate(user, Trigger.DAMAGED, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSprint(PlayerToggleSprintEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer().getUniqueId()).get();

        if (user == null) {
            return;
        }

        this.activate(user, event.isSprinting() ? Trigger.SPRINT_ON : Trigger.SPRINT_OFF, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onUserElementsChange(ElementChangeEvent event) {
        if (event.getHolder() instanceof AbilityUser) {
            Threads.onDelay(() -> ((AbilityUser) event.getHolder()).refresh(), 1);
        }
    }
}
