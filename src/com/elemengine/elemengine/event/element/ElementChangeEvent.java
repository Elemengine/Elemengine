package com.elemengine.elemengine.event.element;

import java.util.Collection;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.ElementHolder;
import com.google.common.collect.ImmutableSet;

public class ElementChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ElementHolder holder;
    private final Set<Element> elements;

    public ElementChangeEvent(ElementHolder holder, Collection<Element> elements) {
        this.holder = holder;
        this.elements = new ImmutableSet.Builder<Element>().addAll(elements).build();
    }

    public ElementHolder getHolder() {
        return holder;
    }

    public Set<Element> getElements() {
        return elements;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
