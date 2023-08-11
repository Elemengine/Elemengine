package com.elementalplugin.elemental.ability.activation;

import org.bukkit.event.Event;

import com.elementalplugin.elemental.ability.AbilityInstance;
import com.elementalplugin.elemental.ability.AbilityUser;

public final class TriggerAction {

    private AbilityUser user;
    private Event provider;
    private AbilityInstance output = null;

    public TriggerAction(AbilityUser user, Event provider) {
        this.user = user;
        this.provider = provider;
    }

    public AbilityUser getUser() {
        return user;
    }

    public Event getProvider() {
        return provider;
    }

    public void setOutput(AbilityInstance instance) {
        output = instance;
    }

    public AbilityInstance getOutput() {
        return output;
    }
}
