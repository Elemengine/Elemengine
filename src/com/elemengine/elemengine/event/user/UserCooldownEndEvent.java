package com.elemengine.elemengine.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.ability.util.Cooldown;

public class UserCooldownEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private AbilityUser user;
    private Cooldown cooldown;

    public UserCooldownEndEvent(AbilityUser user, Cooldown cooldown) {
        this.user = user;
        this.cooldown = cooldown;
    }

    public AbilityUser getUser() {
        return user;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
