package com.elemengine.elemengine.event.user;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;

public class UserRequestSourceEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();

    private final AbilityUser user;
    private final double range;
    private final boolean[] sourceTypes = new boolean[Element.values().length];
    
    private Location sourceLocation = null;
    
    public UserRequestSourceEvent(AbilityUser user, double range, Element required, Element...optionals) {
        this.user = user;
        this.range = range;
        this.sourceTypes[required.ordinal()] = true;
        for (Element optional : optionals) this.sourceTypes[optional.ordinal()] = true;
    }
    
    public AbilityUser getUser() {
        return user;
    }
    
    public double getRange() {
        return range;
    }
    
    public boolean includesSourceType(Element type) {
        return sourceTypes[type.ordinal()];
    }
    
    public Location getSourceLocation() {
        return sourceLocation;
    }
    
    public boolean hasSourceLocation() {
        return sourceLocation != null;
    }
    
    public void setSourceLocation(Location location) {
        this.sourceLocation = location.clone();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
