package com.elemengine.elemengine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.bukkit.event.Listener;

import com.elemengine.elemengine.util.reflect.Dynamics;
import com.elemengine.elemengine.util.spigot.Events;
import com.google.common.base.Preconditions;

public abstract class Manager {

    private static final Comparator<Manager> COMPARE = Comparator.comparingInt(Manager::priority);
    private static final PriorityQueue<Manager> ACTIVE = new PriorityQueue<>(COMPARE);
    private static final Map<Class<? extends Manager>, Manager> CACHE = new HashMap<>();

    /**
     * The priority of this manager, which determines the ordering of startup and
     * tick calls for each registered manager. Lower numbers happen first and are
     * considered lower priority. If order doesn't matter, this can be left as zero.
     * 
     * @return priority of this manager
     */
    protected abstract int priority();

    /**
     * Gives whether this manager needs to be active and have it's tick method
     * called.
     * 
     * @return true if tick method is used
     */
    protected abstract boolean active();

    /**
     * Called when the manager is registered.
     */
    protected abstract void startup();

    /**
     * Called each tick to update the state of the manager.
     */
    protected abstract void tick();

    /**
     * Used to clean up the manager, like when the plugin is disabled.
     */
    protected abstract void clean();

    public static void register(Manager manager) {
        if (CACHE.containsKey(manager.getClass())) {
            return;
        }

        CACHE.put(manager.getClass(), manager);
        manager.startup();
        if (manager instanceof Listener listener) {
            Events.register(listener);
        }

        if (manager.active()) {
            ACTIVE.add(manager);
        }
    }

    public static <T extends Manager> T of(Class<T> clazz) {
        Preconditions.checkArgument(clazz != null, "Cannot get null manager!");
        Preconditions.checkArgument(CACHE.containsKey(clazz), "Attempted to get nonexistant manager of " + clazz.getName());

        return clazz.cast(CACHE.get(clazz));
    }

    static void update() {
        for (Manager man : ACTIVE) {
            man.tick();
        }
    }

    static void onDisable() {
        ACTIVE.forEach(Manager::clean);
        ACTIVE.clear();
    }

    static void init(List<Addon> addons) {
        PriorityQueue<Manager> loaded = new PriorityQueue<>(COMPARE);
        Dynamics.load("com.elemengine.elemengine", Manager.class, loaded::add);
        for (Addon addon : addons) {
            Dynamics.load(addon.getManagerPath(), addon.getClass().getClassLoader(), Manager.class, loaded::add);
        }

        // need to register them in the proper order for everything to work as expected
        Manager next;
        while ((next = loaded.poll()) != null) {
            register(next);
        }
    }
}
