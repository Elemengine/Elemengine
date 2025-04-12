package com.elemengine.elemengine;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ElemenginePlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        Elemengine.init(this);
    }
    
    @Override
    public void onEnable() {
        Elemengine.enable();
    }
    
    @Override
    public void onDisable() {
        Elemengine.destroy();
        HandlerList.unregisterAll(this);
    }
}
