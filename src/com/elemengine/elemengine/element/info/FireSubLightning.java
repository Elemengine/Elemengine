package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class FireSubLightning implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Lightning";
    }

    @Override
    public String getDescription() {
        return "Once a rare subelement, creating and redirecting lightning has become a common element among firebenders.";
    }

    @Override
    public String getChatColor() {
        return "#24adbf";
    }

    @Override
    public Material getMaterial() {
        return Material.LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
