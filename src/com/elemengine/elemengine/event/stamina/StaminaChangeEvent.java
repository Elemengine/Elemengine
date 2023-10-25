package com.elemengine.elemengine.event.stamina;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.util.Stamina;

public class StaminaChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private Stamina stamina;
    private double amount;
    private boolean consumed;

    public StaminaChangeEvent(Stamina stamina, double amount, boolean consumed) {
        this.stamina = stamina;
        this.amount = amount;
        this.consumed = consumed;
    }

    public Stamina getStamina() {
        return stamina;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isConsuming() {
        return consumed;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
