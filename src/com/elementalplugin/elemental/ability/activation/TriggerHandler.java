package com.elementalplugin.elemental.ability.activation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * Used to denote a method as handling a trigger that is passed to the ability
 * info
 */
public @interface TriggerHandler {

    public String value();
}
