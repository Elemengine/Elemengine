package com.elementalplugin.elemental;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.elementalplugin.elemental.listener.ActivationListener;
import com.elementalplugin.elemental.listener.PlayerListener;
import com.elementalplugin.elemental.listener.TempListener;
import com.elementalplugin.elemental.storage.DBConnection;
import com.elementalplugin.elemental.storage.SQLiteDatabase;

public class Elemental extends JavaPlugin {

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
        database.send("CREATE TABLE IF NOT EXISTS t_player (uuid TEXT)");
        database.send("CREATE TABLE IF NOT EXISTS t_player_skills (uuid TEXT, skill_name TEXT, toggled NUMBER, PRIMARY KEY (uuid, skill_name))");
        database.send("CREATE TABLE IF NOT EXISTS t_player_binds (uuid TEXT, bound_slot NUMBER, ability_name TEXT, PRIMARY KEY (uuid, bound_slot))");
    }

    private void setupListeners() {
        this.getServer().getPluginManager().registerEvents(new ActivationListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new TempListener(), this);
    }

    public static Elemental plugin() {
        return JavaPlugin.getPlugin(Elemental.class);
    }

    public static File getFolder() {
        return JavaPlugin.getPlugin(Elemental.class).getDataFolder();
    }

    public static DBConnection database() {
        return database;
    }
}
