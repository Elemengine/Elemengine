package com.elemengine.elemengine.ability.activation;

public enum Trigger {

    LEFT_CLICK ("Left Click", true),
    RIGHT_CLICK_BLOCK ("Right Click Block", true),
    RIGHT_CLICK_ENTITY ("Right Click Entity", true),
    SNEAK_DOWN ("Press Sneak", true),
    SNEAK_UP ("Release Sneak", true),
    SPRINT_ON ("Start Sprinting", false),
    SPRINT_OFF ("Stop Sprinting", false),
    DAMAGED ("Take damage", false),;

    private String display;
    private boolean comboable;

    private Trigger(String display, boolean comboable) {
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
            if (this == a) return true;
        }

        return false;
    }
}