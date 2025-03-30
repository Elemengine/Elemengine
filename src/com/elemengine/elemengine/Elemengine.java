package com.elemengine.elemengine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.elemengine.elemengine.command.Commands;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.database.DBConnection;
import com.elemengine.elemengine.storage.database.SQLiteDatabase;
import com.elemengine.elemengine.util.reflect.Dynamics;
import com.elemengine.elemengine.util.spigot.Events;
import com.elemengine.elemengine.util.spigot.Threads;

public class Elemengine extends JavaPlugin {

    private static final String DB_FILE = "storage.db";
    private static final String ADDONS_FOLDER = "/addons/";
    private static final String ABILITIES_FOLDER = "/abilities/";
    private static final Map<String, Addon> ADDONS = new HashMap<>();
    
    private DBConnection database;

    @Override
    public void onEnable() {
        database = new DBConnection(new SQLiteDatabase(new File(this.getDataFolder(), DB_FILE)));
        
        database.send(
            "CREATE TABLE IF NOT EXISTS t_player (uuid TEXT)",
            "CREATE TABLE IF NOT EXISTS t_player_elements (uuid TEXT, element_name TEXT, toggled NUMBER, PRIMARY KEY (uuid, element_name))",
            "CREATE TABLE IF NOT EXISTS t_player_binds (uuid TEXT, bound_slot NUMBER, ability_name TEXT, PRIMARY KEY (uuid, bound_slot))",
            "CREATE TABLE IF NOT EXISTS t_ability_ids (id INTEGER, ability_name TEXT, PRIMARY KEY (id, ability_name))",
            "CREATE TABLE IF NOT EXISTS t_player_abilities (uuid TEXT, ability_id INTEGER, PRIMARY KEY (uuid, ability_id))"
        ).thenRun(() -> {
            Events.register(new Sourcing());
            List<Addon> loaded = new ArrayList<>();
            Dynamics.loadDir(Elemengine.getAddonsFolder(), true, Addon.class, loaded::add);
            Manager.init(loaded);
            
            for (Addon addon : loaded) {
                load(addon, true);
                Elemengine.plugin().getLogger().info("Loaded addon " + addon.getName() + " v" + addon.getVersion() + " by " + addon.getAuthor());
            }
            
            Threads.onTimer(Manager::update, 1, 0);
        }).join();
    }

    @Override
    public void onDisable() {
        for (Addon addon : ADDONS.values()) {
            addon.cleanup();
        }
        ADDONS.clear();
        Manager.onDisable();
        HandlerList.unregisterAll(this);
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

    public DBConnection getDatabase() {
        return database;
    }

    public static Elemengine plugin() {
        return JavaPlugin.getPlugin(Elemengine.class);
    }
    
    public static Logger logger() {
        return plugin().getLogger();
    }

    public static File getFolder() {
        return plugin().getDataFolder();
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

    public static DBConnection database() {
        return plugin().getDatabase();
    }

    public static Optional<Addon> getAddon(String name) {
        return Optional.ofNullable(ADDONS.get(name.toLowerCase().replace(' ', '_')));
    }
    
    public static List<Addon> listAddons() {
        return new ArrayList<>(ADDONS.values());
    }
}
