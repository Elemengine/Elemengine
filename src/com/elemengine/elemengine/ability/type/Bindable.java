package com.elemengine.elemengine.ability.type;

import org.bukkit.event.Event;

import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.ability.activation.Trigger;

public interface Bindable {
    
    public AbilityInstance<?> createBindInstance(AbilityUser user, Trigger trigger, Event event);

    public String getBindUsage();

}
