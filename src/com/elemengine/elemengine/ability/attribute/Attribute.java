package com.elemengine.elemengine.ability.attribute;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Attribute {

    String value() default "";
    
    boolean auto() default true;
    String autoField() default "";

    // Common attributes
    // !!! when adding an attribute here, add a copy in AbilityInstance.java for ease of use !!!
    static final String SPEED = "speed";
    static final String RANGE = "range";
    static final String SELECT_RANGE = "selectRange";
    static final String DAMAGE = "damage";
    static final String COOLDOWN = "cooldown";
    static final String DURATION = "duration";
    static final String RADIUS = "radius";
    static final String CHARGE_TIME = "chargeTime";
    static final String WIDTH = "width";
    static final String HEIGHT = "height";
    static final String KNOCKBACK = "knockback";
    static final String KNOCKUP = "knockup";
    static final String FIRE_TICK = "fireTick";

}
