package com.elemengine.elemengine.element;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elemengine.elemengine.event.element.ElementChangeEvent;
import com.elemengine.elemengine.util.spigot.Events;
import com.google.common.collect.ImmutableSet;

public abstract class ElementHolder {

    public static enum ElementToggleResult {
        ON, OFF, INVALID;
    }

    private ImmutableSet<Element> elements = null;
    private Set<Element> toggled;

    public ElementHolder() {
        this.elements = ImmutableSet.of();
        this.toggled = new HashSet<>();
    }

    public ElementHolder(Collection<Element> elements) {
        this(elements, Collections.emptySet());
    }

    public ElementHolder(Collection<Element> elements, Collection<Element> toggled) {
        this.elements = new ImmutableSet.Builder<Element>().addAll(elements).build();
        this.toggled = new HashSet<>(toggled);
    }

    /**
     * Sets the elements of this ElementHolder to the given Elements
     * 
     * @param elements new elements
     */
    public final void setElements(Element... elements) {
        Events.call(new ElementChangeEvent(this, Arrays.asList(elements)));
        this.elements = new ImmutableSet.Builder<Element>().add(elements).build();
    }

    /**
     * Sets the elements of this ElementHolder to the given collection of Elements
     * 
     * @param elements new elements
     */
    public final void setElements(Collection<Element> elements) {
        Events.call(new ElementChangeEvent(this, elements));
        this.elements = new ImmutableSet.Builder<Element>().addAll(elements).build();
    }

    public final Set<Element> getElements() {
        return elements;
    }

    /**
     * Add a Element to this ElementHolder if not already present
     * 
     * @param element new element to add
     * @return false if already has sill, true otherwise
     */
    public final boolean addElement(Element element) {
        if (elements.contains(element)) {
            return false;
        }

        ImmutableSet.Builder<Element> builder = new ImmutableSet.Builder<Element>().add(element);

        if (elements != null) {
            builder.addAll(elements);
        }

        ImmutableSet<Element> newSet = builder.build();
        Events.call(new ElementChangeEvent(this, newSet));
        this.elements = newSet;
        return true;
    }

    /**
     * Remove a Element from this ElementHolder if present
     * 
     * @param element old element to remove
     * @return false if element is not present, true otherwise
     */
    public final boolean removeElement(Element element) {
        if (elements == null || !elements.contains(element)) {
            return false;
        }

        ImmutableSet.Builder<Element> builder = new ImmutableSet.Builder<>();

        for (Element curr : elements) {
            if (!curr.equals(element)) {
                builder.add(curr);
            }
        }

        ImmutableSet<Element> newSet = builder.build();
        Events.call(new ElementChangeEvent(this, newSet));
        this.elements = newSet;
        return true;
    }

    /**
     * Returns whether this {@link ElementHolder} has the given {@link Element}
     * 
     * @param element being checked for
     * @return true if element is present, false if not
     */
    public final boolean hasElement(Element element) {
        return elements.contains(element);
    }

    /**
     * Returns whether this ElementHolder has the given Elements
     * 
     * @param elements what to check for
     * @param all    true if all are necessary, any number matching otherwise
     * @return true if elements match
     */
    public final boolean hasElements(Collection<Element> elements, boolean all) {
        return all ? this.elements.containsAll(elements) : this.elements.stream().anyMatch(elements::contains);
    }

    /**
     * Toggle the given element on / off if the ElementHolder has it
     * 
     * @param element being toggled
     * @return the result of the attempted toggle
     */
    public final ElementToggleResult toggle(Element element) {
        if (!elements.contains(element)) {
            return ElementToggleResult.INVALID;
        }

        if (!toggled.add(element)) {
            toggled.remove(element);
            return ElementToggleResult.ON;
        }

        return ElementToggleResult.OFF;
    }

    /**
     * Returns whether this ElementHolder has the given Element toggled off
     * 
     * @param element being checked for toggle
     * @return true if toggled off, false otherwise
     */
    public final boolean isToggled(Element element) {
        return toggled.contains(element);
    }
}
