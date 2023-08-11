package com.elementalplugin.elemental.ability;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.elementalplugin.elemental.ability.attribute.Attribute;
import com.elementalplugin.elemental.ability.attribute.AttributeGroup;
import com.elementalplugin.elemental.ability.attribute.Modifier;
import com.elementalplugin.elemental.effect.Effect;
import com.elementalplugin.elemental.effect.ParticleData;
import com.elementalplugin.elemental.effect.Effect.EffectBuilder;
import com.elementalplugin.elemental.util.reflect.Safety;

public abstract class AbilityInstance {

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

    static final Map<Class<? extends AbilityInstance>, Map<String, Field>> ATTRIBUTES = new HashMap<>();

    public enum State {
        UPDATING, STOPPING;
    }

    protected final AbilityInfo provider;
    protected final AbilityUser user;
    private int counter = -1;
    private long startTime = -1;
    private Map<Field, Modifier> mods = new HashMap<>();
    private State state = State.UPDATING;

    public AbilityInstance(AbilityInfo provider, AbilityUser user) {
        this.provider = provider;
        this.user = user;
    }

    public final AbilityInfo getProvider() {
        return provider;
    }

    /**
     * Gets the user associated with this instance
     * 
     * @return user
     */
    public final AbilityUser getUser() {
        return user;
    }

    public final State getState() {
        return state;
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
    
    public String getName() {
        return provider.getName();
    }

    public boolean hasUpdate() {
        return true;
    }

    final boolean start() {
        startTime = System.currentTimeMillis();
        user.active.add(this);

        for (Entry<Field, Modifier> entry : mods.entrySet()) {
            try {
                new Safety(this, entry.getKey()).getSet((value) -> {
                    return entry.getValue().apply(value);
                }, true);
            } catch (Exception e) {}
        }

        return onStart();
    }

    final boolean update(double timeDelta) {
        ++counter;
        return onUpdate(timeDelta);
    }

    final void stop() {
        onStop();
        state = State.STOPPING;
        user.active.remove(this);
        startTime = -1;
        counter = -1;
        mods.clear();
    }

    public boolean hasAttribute(String attribute) {
        return ATTRIBUTES.containsKey(this.getClass()) && ATTRIBUTES.get(this.getClass()).containsKey(attribute);
    }

    public boolean addModifier(String attribute, Modifier mod) {
        if (!this.hasAttribute(attribute)) {
            return false;
        }

        Field field = ATTRIBUTES.get(this.getClass()).get(attribute);
        mods.compute(field, (k, v) -> mod.and(v));
        return true;
    }

    public boolean[] addModifier(AttributeGroup group, Modifier mod) {
        boolean[] worked = new boolean[group.size()];
        int i = -1;

        for (String attribute : group.getAttributes()) {
            worked[++i] = this.addModifier(attribute, mod);
        }

        return worked;
    }

    public EffectBuilder effect() {
        return effect(null);
    }

    public EffectBuilder effect(Consumer<ParticleData> mods) {
        ParticleData data = user.getSkillParticle(provider.getSkill());

        if (mods != null) {
            mods.accept(data);
        }

        return Effect.builder().add(data);
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
}
