package com.elemengine.elemengine.event.user;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.AbilityUser;

public class UserCanUseAbilityEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final AbilityUser user;
    private final AbilityInfo info;
    
    private boolean cancelled = false;
    
    public UserCanUseAbilityEvent(AbilityUser user, AbilityInfo info) {
        this.user = user;
        this.info = info;
    }
    
    public final AbilityUser getUser() {
        return user;
    }
    
    public final AbilityInfo getAbility() {
        return info;
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
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
