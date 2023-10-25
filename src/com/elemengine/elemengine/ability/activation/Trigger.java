package com.elemengine.elemengine.ability.activation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.elemengine.elemengine.Elemengine;

public class Trigger {

    private static final Map<String, Trigger> CACHE = new HashMap<>();
    
    public static final String ID_LEFT_CLICK = "left_click";
    public static final String ID_RIGHT_CLICK_BLOCK = "right_click_block";
    public static final String ID_RIGHT_CLICK_ENTITY = "right_click_entity";
    public static final String ID_SNEAK_DOWN = "sneak_down";
    public static final String ID_SNEAK_UP = "sneak_up";
    public static final String ID_SPRINT_ON = "sprint_on";
    public static final String ID_SPRINT_OFF = "sprint_off";
    public static final String ID_DAMAGED = "damaged";
    public static final String ID_PASSIVE = "passive";
    public static final String ID_COMBO = "combo";

    public static final Trigger LEFT_CLICK = register(ID_LEFT_CLICK, "Left Click", true);
    public static final Trigger RIGHT_CLICK_BLOCK = register(ID_RIGHT_CLICK_BLOCK, "Right Click Block", true);
    public static final Trigger RIGHT_CLICK_ENTITY = register(ID_RIGHT_CLICK_ENTITY, "Right Click Entity", true);
    public static final Trigger SNEAK_DOWN = register(ID_SNEAK_DOWN, "Press Sneak", true);
    public static final Trigger SNEAK_UP = register(ID_SNEAK_UP, "Release Sneak", true);
    public static final Trigger SPRINT_ON = register(ID_SPRINT_ON, "Start Sprinting", false);
    public static final Trigger SPRINT_OFF = register(ID_SPRINT_OFF, "Stop Sprinting", false);
    public static final Trigger DAMAGED = register(ID_DAMAGED, "Take damage", false);
    public static final Trigger PASSIVE = register(ID_PASSIVE, "Passive", false);
    public static final Trigger COMBO = register(ID_COMBO, "Combo sequence", false);

    private String id, display;
    private boolean comboable;

    private Trigger(String id, String display, boolean comboable) {
        this.id = id;
        this.display = display;
        this.comboable = comboable;
    }

    public String getDisplay() {
        return display;
    }

    public boolean canCombo() {
        return comboable;
    }

    public boolean matchAny(Trigger... triggers) {
        for (Trigger a : triggers) {
            if (this == a)
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Trigger && other.toString().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Attempts to register the given values in a trigger. If a trigger from the
     * given id (case insensitive) is found, the existing trigger will be returned
     * instead.
     * 
     * @param id
     * @param display
     * @param comboable
     * @return
     */
    public static Trigger register(String id, String display, boolean comboable) {
        if (id == null) {
            Elemengine.plugin().getLogger().warning("Attempted register of activation with null id");
            return null;
        }

        return CACHE.computeIfAbsent(id.toLowerCase(), (s) -> new Trigger(s, display, comboable));
    }

    public static Optional<Trigger> of(String id) {
        return Optional.ofNullable(CACHE.get(id.toLowerCase()));
    }
}