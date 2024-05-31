package com.elemengine.elemengine.ability.type.combo;

import java.util.Collection;
import java.util.Iterator;

import com.elemengine.elemengine.ability.activation.Trigger;

public class SequenceInfo {

    private String ability;
    private Trigger trigger;

    private SequenceInfo(String ability, Trigger trigger) {
        this.ability = ability;
        this.trigger = trigger;
    }

    public String getAbility() {
        return ability;
    }

    public boolean abilityEquals(String other) {
        return ability.equalsIgnoreCase(other);
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public boolean triggerEquals(Trigger other) {
        return trigger == other;
    }

    /**
     * Compare the contents of this and another {@link SequenceInfo} to check if
     * they match
     * 
     * @param other The other {@link SequenceInfo}
     * @return true if contents match
     */
    public boolean matches(SequenceInfo other) {
        return ability == other.ability && trigger == other.trigger;
    }

    /**
     * Compare the contents of this {@link SequenceInfo} and the given
     * {@link Ability} and {@link Activation}
     * 
     * @param ability The {@link Ability} to compare against
     * @param trigger The {@link Activation} to compare against
     * @return true if contents match the given parameters
     */
    public boolean matches(String ability, Trigger trigger) {
        return this.abilityEquals(ability) && this.trigger == trigger;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SequenceInfo) {
            return matches((SequenceInfo) other);
        }

        return false;
    }

    @Override
    public String toString() {
        return "[" + ability + ":" + trigger.toString() + "]";
    }

    public String toDisplay() {
        return trigger.getDisplay() + " " + ability;
    }

    public static String stringify(Collection<SequenceInfo> infos) {
        String strung = "";
        Iterator<SequenceInfo> iter = infos.iterator();
        while (iter.hasNext()) {
            strung += iter.next().toString() + (iter.hasNext() ? "+" : "");
        }

        return strung;
    }

    public static SequenceInfo of(String ability, Trigger trigger) {
        if (ability == null || trigger == null) {
            return null;
        }

        return new SequenceInfo(ability, trigger);
    }
}
