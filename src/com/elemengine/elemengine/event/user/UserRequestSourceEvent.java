package com.elemengine.elemengine.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;

public class UserRequestSourceEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final AbilityUser user;
    private final Element element;
    
    private int amount;
    private boolean fulfilled = false;
    
    public UserRequestSourceEvent(AbilityUser user, Element element, int amount) {
        this.user = user;
        this.element = element;
        this.amount = amount;
    }
    
    public AbilityUser getUser() {
        return user;
    }
    
    public Element getElement() {
        return element;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public boolean isFulfilled() {
        return fulfilled;
    }
    
    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
