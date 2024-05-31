package com.elemengine.elemengine.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.command.Commands;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.util.Events;
import com.elemengine.elemengine.util.reflect.DynamicLoader;

public class Addons extends Manager {
    
    private final Map<String, Addon> addons = new HashMap<>();
    
    private void load(Addon addon, boolean cache) {
        if (cache) {
            addons.put(addon.getInternalName(), addon);
        }
        
        addon.startup();
        Config.process(addon);
        Events.register(addon, Elemengine.plugin());
        
        for (SubCommand cmd : addon.commandRegistrator().get()) {
            addon.cmdIds.add(Commands.manager().register(cmd));
        }
    }

    @Override
    protected int priority() {
        return 1000;
    }

    @Override
    protected boolean active() {
        return false;
    }

    @Override
    protected void startup() {
        DynamicLoader.loadDir(Elemengine.plugin(), Elemengine.getAddonsFolder(), true, Addon.class::isAssignableFrom, clazz -> {
            try {
                Addon addon = (Addon) clazz.getDeclaredConstructor().newInstance();
                if (addons.containsKey(addon.getInternalName())) {
                    Elemengine.plugin().getLogger().warning("Unable to load addon from class '" + clazz.getName() + "', another addon is using the same name."); 
                    return;
                }
                
                this.load(addon, true);
                Elemengine.plugin().getLogger().info("Loaded addon " + addon.getName() + " v" + addon.getVersion() + " by " + addon.getAuthor());
            } catch (Exception e) {
                Elemengine.plugin().getLogger().warning("Error while loading addon from class '" + clazz.getName() + "'");
                e.printStackTrace();
                return;
            }
        });
    }

    @Override
    protected void tick() {}

    @Override
    protected void clean() {
        for (Addon addon : addons.values()) {
            addon.cleanup();
        }
        addons.clear();
    }
    
    public void reload(Addon addon) {
        addon.cleanup();
        Events.unregister(addon);
        
        for (UUID uuid : addon.cmdIds) {
            Commands.manager().unregister(uuid);
        }
        
        this.load(addon, false);
    }

    public Optional<Addon> tryFrom(String name) {
        return Optional.ofNullable(addons.get(name.toLowerCase().replace(' ', '_')));
    }
    
    public List<Addon> list() {
        return new ArrayList<>(addons.values());
    }
    
    public static Addons manager() {
        return Manager.of(Addons.class);
    }
}
