package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class FireElement implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Fire";
    }

    @Override
    public String getDescription() {
        return "Fire is the element of power, preferring to attack aggressively and overwhelm their opponents.";
    }

    @Override
    public String getChatColor() {
        return "#f42a10";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
