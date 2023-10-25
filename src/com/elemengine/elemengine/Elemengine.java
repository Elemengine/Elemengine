package com.elemengine.elemengine;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.elemengine.elemengine.listener.ActivationListener;
import com.elemengine.elemengine.listener.PlayerListener;
import com.elemengine.elemengine.listener.TempListener;
import com.elemengine.elemengine.storage.DBConnection;
import com.elemengine.elemengine.storage.SQLiteDatabase;

public class Elemengine extends JavaPlugin {

    private static DBConnection database;

    @Override
    public void onEnable() {
        this.setupDatabase();
        Manager.init();
        this.setupListeners();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Manager::update, 0, 1);
    }

    @Override
    public void onDisable() {
        Manager.onDisable();
    }

    private void setupDatabase() {
        database = new DBConnection(new SQLiteDatabase(new File(this.getDataFolder(), "storage.db")));
        database.send("CREATE TABLE IF NOT EXISTS t_player (uuid TEXT)").join();
        database.send("CREATE TABLE IF NOT EXISTS t_player_skills (uuid TEXT, skill_name TEXT, toggled NUMBER, PRIMARY KEY (uuid, skill_name))").join();
        database.send("CREATE TABLE IF NOT EXISTS t_player_binds (uuid TEXT, bound_slot NUMBER, ability_name TEXT, PRIMARY KEY (uuid, bound_slot))").join();
        database.send("CREATE TABLE IF NOT EXISTS t_ability_ids (id INTEGER PRIMARY KEY, ability_name TEXT)").join();
        database.send("CREATE TABLE IF NOT EXISTS t_player_abilities (uuid TEXT, ability_id INTEGER, PRIMARY KEY (uuid, ability_name))").join();
    }

    private void setupListeners() {
        this.getServer().getPluginManager().registerEvents(new ActivationListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new TempListener(), this);
    }

    public static Elemengine plugin() {
        return JavaPlugin.getPlugin(Elemengine.class);
    }

    public static File getFolder() {
        return JavaPlugin.getPlugin(Elemengine.class).getDataFolder();
    }
    
    public static File getAddonsFolder() {
        File addons = new File(getFolder(), "/addons/");
        
        if (!addons.exists()) {
            addons.mkdirs();
        }
        
        return addons;
    }

    public static DBConnection database() {
        return database;
    }
}
