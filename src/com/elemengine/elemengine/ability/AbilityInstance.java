package com.elemengine.elemengine.ability;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;

import com.elemengine.elemengine.ability.attribute.Attribute;
import com.elemengine.elemengine.ability.attribute.AttributeGroup;
import com.elemengine.elemengine.ability.attribute.Modifier;
import com.elemengine.elemengine.event.ability.AttributesRecalculateEvent;
import com.elemengine.elemengine.util.reflect.Fields;
import com.elemengine.elemengine.util.spigot.Events;

public abstract class AbilityInstance {
    
    static final Map<Class<? extends AbilityInstance>, Map<String, Field>> ATTRIBUTE_FIELDS = new HashMap<>();
    
    /**
     * Represents what state the instance exists in. Typically instances will go through
     * the phases in order of STARTING -> UPDATING -> STOPPING. An instance
     * will skip the UPDATING phase if {@link AbilityInstance#onStart()} returns false
     */
    public enum Phase {
        /**
         * State when the instance is being started
         */
        STARTING,
        /**
         * State when the instance is being updated
         */
        UPDATING,
        /**
         * State when the instance is being stopped
         */
        STOPPING
    }

    protected final AbilityInfo provider;
    protected final AbilityUser user;
    protected final World world;
    
    private final Map<Field, Object> base = new HashMap<>();
    private final Map<Field, Modifier> mods = new HashMap<>();
    
    private Phase state = Phase.STARTING;
    private int counter = -1;
    private long startTime = -1;
    private boolean recalculating = false;

    public AbilityInstance(AbilityInfo provider, AbilityUser user) {
        this.provider = provider;
        this.user = user;
        this.world = user.getWorld();
    }

    final void start() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Attribute.class)) continue;
            
            Attribute attr = field.getAnnotation(Attribute.class);
            if (attr.auto()) {
                String autoField = attr.autoField();
                if (autoField.isBlank()) {
                    autoField = field.getName();
                }
                
                Fields.get(provider, autoField).ifPresent(v -> Fields.set(this, field, v));
            }
            Fields.get(this, field).ifPresent(value -> base.put(field, value));
        }
        
        startTime = System.currentTimeMillis();
        
        if (this.onStart()) {
            user.active.add(this);
            state = Phase.UPDATING;
        } else {
            state = Phase.STOPPING;
        }
    }

    final boolean update(double timeDelta) {
        ++counter;
        return onUpdate(timeDelta);
    }

    final void stop() {
        if (state == Phase.STOPPING) return;
        
        state = Phase.STOPPING;
        this.onStop();
        user.active.remove(this);
        startTime = -1;
        counter = -1;
    }

    public final AbilityInfo getProvider() {
        return provider;
    }

    public final AbilityUser getUser() {
        return user;
    }
    
    public final Phase getPhase() {
        return state;
    }

    public final boolean addAttributeModifier(String attribute, Modifier mod) {
        if (!this.hasAttribute(attribute)) {
            return false;
        }

        Field field = ATTRIBUTE_FIELDS.get(this.getClass()).get(attribute);
        mods.compute(field, (k, v) -> mod.and(v));
        return true;
    }

    public final boolean[] addAttributeModifier(AttributeGroup group, Modifier mod) {
        boolean[] worked = new boolean[group.size()];
        int i = -1;

        for (String attribute : group.getAttributes()) {
            worked[++i] = this.addAttributeModifier(attribute, mod);
        }

        return worked;
    }
    
    public final void recalculateAttributes() {
        if (recalculating) throw new RuntimeException("Attributes already recalculating exception.");
        recalculating = true;
        
        if (!Events.call(new AttributesRecalculateEvent(this)).isCancelled()) {
            Iterator<Entry<Field, Modifier>> iter = mods.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Field, Modifier> entry = iter.next();
                Fields.set(this, entry.getKey(), entry.getValue().apply(base.get(entry.getKey())));
                iter.remove();
            }
        }
        
        recalculating = false;
    }
    
    public final boolean hasAttribute(String attribute) {
        return ATTRIBUTE_FIELDS.containsKey(this.getClass()) && ATTRIBUTE_FIELDS.get(this.getClass()).containsKey(attribute);
    }

    /**
     * Gets how many ticks have passed since this instance was started, will return
     * {@code -1} if called before the instance has started
     * 
     * @return ticks since starting
     */
    public final int ticksLived() {
        return counter;
    }

    /**
     * Gets the time in milliseconds when this instance was started, will return
     * {@code -1} if called before the instance has started
     * 
     * @return instance start time
     */
    public final long startTime() {
        return startTime;
    }

    /**
     * Gets the time in millis since this instance was started
     * 
     * @return instance duration
     */
    public final long timeLived() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Return whether or not this instance has been started
     * 
     * @return has instance started
     */
    public final boolean hasStarted() {
        return startTime >= 0;
    }
    
    public boolean hasUpdate() {
        return true;
    }
    
    public String getName() {
        return provider.getName();
    }

    /**
     * Method called when the instance is started
     * 
     * @return true if ability can successfully start
     */
    protected abstract boolean onStart();

    /**
     * Method called to update the instance
     * 
     * @param timeDelta the time difference between update calls, in seconds
     * @return false to stop updating
     */
    protected abstract boolean onUpdate(double timeDelta);

    /**
     * Method called when instance is stopped
     */
    protected abstract void onStop();

    // QoL copies of common attributes
    protected static final String SPEED = Attribute.SPEED;
    protected static final String RANGE = Attribute.RANGE;
    protected static final String SELECT_RANGE = Attribute.SELECT_RANGE;
    protected static final String DAMAGE = Attribute.DAMAGE;
    protected static final String COOLDOWN = Attribute.COOLDOWN;
    protected static final String DURATION = Attribute.DURATION;
    protected static final String RADIUS = Attribute.RADIUS;
    protected static final String CHARGE_TIME = Attribute.CHARGE_TIME;
    protected static final String WIDTH = Attribute.WIDTH;
    protected static final String HEIGHT = Attribute.HEIGHT;
    protected static final String KNOCKBACK = Attribute.KNOCKBACK;
    protected static final String KNOCKUP = Attribute.KNOCKUP;
    protected static final String FIRE_TICK = Attribute.FIRE_TICK;
}
