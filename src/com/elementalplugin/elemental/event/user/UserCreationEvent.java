package com.elementalplugin.elemental.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elementalplugin.elemental.ability.AbilityUser;

public class UserCreationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private AbilityUser user;

    public UserCreationEvent(AbilityUser user) {
        this.user = user;
    }

    public AbilityUser getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
