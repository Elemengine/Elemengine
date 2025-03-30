package com.elemengine.elemengine.event.ability;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.ability.attribute.AttributeGroup;
import com.elemengine.elemengine.ability.attribute.Modifier;

public class AttributesRecalculateEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final AbilityInstance instance;
    
    private boolean cancelled = false;
    
    public AttributesRecalculateEvent(AbilityInstance instance) {
        this.instance = instance;
    }
    
    public boolean addAttributeModifier(String attribute, Modifier mod) {
        return instance.addAttributeModifier(attribute, mod);
    }
    
    public boolean[] addGroupModifier(AttributeGroup group, Modifier mod) {
        return instance.addAttributeModifier(group, mod);
    }
    
    public boolean hasAttribute(String attribute) {
        return instance.hasAttribute(attribute);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
