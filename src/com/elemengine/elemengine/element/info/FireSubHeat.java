package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class FireSubHeat implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Heat";
    }

    @Override
    public String getDescription() {
        return "Heatbending ";
    }

    @Override
    public String getChatColor() {
        return "#ad3434";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
