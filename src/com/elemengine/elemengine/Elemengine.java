package com.elemengine.elemengine;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.elemengine.elemengine.listener.ActivationListener;
import com.elemengine.elemengine.listener.PlayerListener;
import com.elemengine.elemengine.listener.TempListener;
import com.elemengine.elemengine.storage.database.DBConnection;
import com.elemengine.elemengine.storage.database.SQLiteDatabase;

public class Elemengine extends JavaPlugin {

    private static final String DB_FILE = "storage.db";
    private static final String ADDONS_FOLDER = "/addons/";
    private static final String ABILITIES_FOLDER = "/abilities/";

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
        ).thenAccept(ints -> {
            for (int i = 0; i < ints.length; ++i) {
                if (ints[i] < 0) this.getLogger().warning("Database setup command #" + (i + 1) + " failed.");
                else this.getLogger().info("Database setup command #" + (i + 1) + " completed.");
            }
        }).thenRun(() -> {
            Manager.init();
            this.setupListeners();
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Manager::update, 0, 1);
        }).join();
    }

    @Override
    public void onDisable() {
        Manager.onDisable();
    }

    private void setupListeners() {
        this.getServer().getPluginManager().registerEvents(new ActivationListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new TempListener(), this);
    }

    public DBConnection getDatabase() {
        return database;
    }

    public static Elemengine plugin() {
        return JavaPlugin.getPlugin(Elemengine.class);
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
}
