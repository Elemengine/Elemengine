package com.elementalplugin.elemental.event.user;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elementalplugin.elemental.ability.AbilityInfo;
import com.elementalplugin.elemental.ability.AbilityUser;

public class UserBindChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private AbilityUser user;
    private int slot;
    private AbilityInfo result;

    public UserBindChangeEvent(AbilityUser user, int slot, AbilityInfo result) {
        this.user = user;
        this.slot = slot;
        this.result = result;
    }

    public AbilityUser getUser() {
        return user;
    }

    public int getSlot() {
        return slot;
    }

    /**
     * Gets the current result of this bind change event.
     * 
     * @return null if the slot is being cleared, otherwise
     */
    public AbilityInfo getResult() {
        return result;
    }

    /**
     * Sets the result of this bind change event to the given ability
     * 
     * @param result null to clear the slot, otherwise the ability to bind
     */
    public void setResult(AbilityInfo result) {
        this.result = result;
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
