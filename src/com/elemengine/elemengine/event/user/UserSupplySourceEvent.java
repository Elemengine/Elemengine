package com.elemengine.elemengine.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;

public class UserSupplySourceEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final AbilityUser user;
    private final Element element;
    private int amount;
    
    public UserSupplySourceEvent(AbilityUser user, Element element, int amount) {
        this.user = user;
        this.element = element;
        this.amount = amount;
    }
    
    public final AbilityUser getUser() {
        return user;
    }
    
    public final Element getElement() {
        return element;
    }
    
    public final int getAmount() {
        return amount;
    }
    
    public final void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
