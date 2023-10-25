package com.elemengine.elemengine.ability.attribute;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Attribute {

    public String value();

    // Common attributes
    // !!! when adding an attribute here, add a copy in AbilityInstance.java for
    // ease of use !!!
    public static final String SPEED = "speed";
    public static final String RANGE = "range";
    public static final String SELECT_RANGE = "select_range";
    public static final String DAMAGE = "damage";
    public static final String COOLDOWN = "cooldown";
    public static final String DURATION = "duration";
    public static final String RADIUS = "radius";
    public static final String CHARGE_TIME = "charge_time";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String KNOCKBACK = "knockback";
    public static final String KNOCKUP = "knockup";
    public static final String FIRE_TICK = "fire_tick";

}
