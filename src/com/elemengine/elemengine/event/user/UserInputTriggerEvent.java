package com.elemengine.elemengine.event.user;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.ability.activation.Trigger;

public class UserInputTriggerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private AbilityUser user;
    private Trigger trigger;
    private Event provider;

    public UserInputTriggerEvent(AbilityUser user, Trigger trigger, Event provider) {
        this.user = user;
        this.trigger = trigger;
        this.provider = provider;
    }

    public AbilityUser getUser() {
        return user;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Event getProvider() {
        return provider;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
