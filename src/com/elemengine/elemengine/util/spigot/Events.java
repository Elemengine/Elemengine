package com.elemengine.elemengine.util.spigot;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.elemengine.elemengine.Elemengine;
import com.google.common.base.Preconditions;

public final class Events {

    private static Listener DUMMY = new Listener() {};

    private Events() {}

    public static <T extends Event> T call(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
    
    public static void register(Listener...listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Elemengine.plugin());
        }
    }
    
    public static void register(JavaPlugin provider, Listener...listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, provider);
        }
    }

    public static void unregister(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static <T extends Event> EventSubscriber<T> subscribe(Class<T> event) {
        return new EventSubscriber<T>(event, Elemengine.plugin());
    }

    public static <T extends Event> EventSubscriber<T> subscribe(Class<T> event, JavaPlugin provider) {
        return new EventSubscriber<T>(event, provider);
    }

    public static class EventSubscriber<T extends Event> {

        private Class<T> type;
        private JavaPlugin provider;
        private EventPriority priority = EventPriority.NORMAL;
        private boolean ignoreCanceled = true, built = false;

        private EventSubscriber(Class<T> type, JavaPlugin provider) {
            this.type = type;
            this.provider = provider;
        }

        public EventSubscriber<T> ignoreCanceled(boolean ignoreCanceled) {
            this.ignoreCanceled = ignoreCanceled;
            return this;
        }

        public EventSubscriber<T> priority(EventPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Registers the handler for this subscriber. Can only be done once per
         * subscriber.
         * 
         * @param handle What to do with events handled by this subscriber
         */
        public void handler(Consumer<T> handle) {
            Preconditions.checkState(!built, "Attempted to handle subscription more than once!");

            built = true;
            provider.getServer().getPluginManager().registerEvent(type, DUMMY, priority, (l, e) -> {
                if (l != DUMMY || !type.isAssignableFrom(e.getClass())) {
                    return;
                }

                handle.accept(type.cast(e));
            }, provider, ignoreCanceled);
        }
    }
}
