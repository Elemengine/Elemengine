package com.elemengine.elemengine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.elemengine.elemengine.command.Commands;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.database.Database;
import com.elemengine.elemengine.storage.database.sqlite.SQLiteDatabase;
import com.elemengine.elemengine.util.reflect.Dynamics;
import com.elemengine.elemengine.util.spigot.Events;
import com.elemengine.elemengine.util.spigot.Threads;

public class Elemengine {

    private static final String ADDONS_FOLDER = "/addons/";
    private static final String ABILITIES_FOLDER = "/abilities/";
    private static final Map<String, Addon> ADDONS = new HashMap<>();
    
    private static ElemenginePlugin plugin;
    private static Database database;
    
    static void init(ElemenginePlugin provider) {
        plugin = provider;
    }
    
    static void enable() {
        database = new SQLiteDatabase();
        database.setup();
        
        Events.register(new Sourcing());
        List<Addon> loaded = new ArrayList<>();
        Dynamics.loadDir(Elemengine.getAddonsFolder(), true, Addon.class, loaded::add);
        Manager.init(loaded);
        
        for (Addon addon : loaded) {
            load(addon, true);
            logger().info("Loaded addon " + addon.getName() + " v" + addon.getVersion() + " by " + addon.getAuthor());
        }
        
        Threads.onTimer(Manager::update, 1, 0);
    }
    
    static void destroy() {
        for (Addon addon : ADDONS.values()) {
            addon.cleanup();
        }
        ADDONS.clear();
        Manager.onDisable();
    }
    
    private static void load(Addon addon, boolean cache) {
        if (cache) {
            ADDONS.put(addon.getInternalName(), addon);
        }
        
        addon.startup();
        Events.register(Config.process(addon));
        
        for (SubCommand cmd : addon.commandRegistrator().get()) {
            addon.cmdIds.add(Commands.manager().register(cmd));
        }
    }
    
    public static void reload(Addon addon) {
        addon.cleanup();
        Events.unregister(addon);
        
        for (UUID uuid : addon.cmdIds) {
            Commands.manager().unregister(uuid);
        }
        
        Elemengine.load(addon, false);
    }

    public static Database database() {
        return database;
    }

    public static ElemenginePlugin plugin() {
        return plugin;
    }
    
    public static Logger logger() {
        return plugin.getLogger();
    }

    public static File getFolder() {
        return plugin.getDataFolder();
    }
    
    public static File getAddonsFolder() {
        File addons = new File(getFolder(), ADDONS_FOLDER);
        
        if (!addons.exists()) {
            addons.mkdirs();
        }
        
        return addons;
    }
    
    public static File getAbilitiesFolder() {
        File abilities = new File(getFolder(), ABILITIES_FOLDER);
        
        if (!abilities.exists()) {
            abilities.mkdirs();
        }
        
        return abilities;
    }

    public static Optional<Addon> getAddon(String name) {
        return Optional.ofNullable(ADDONS.get(name.toLowerCase().replace(' ', '_')));
    }
    
    public static List<Addon> listAddons() {
        return new ArrayList<>(ADDONS.values());
    }
}
