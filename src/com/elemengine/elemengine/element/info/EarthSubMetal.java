package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class EarthSubMetal implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Metal";
    }

    @Override
    public String getDescription() {
        return "Metalbenders are able to control most metals by bending the small pieces of earth within them.";
    }

    @Override
    public String getChatColor() {
        return "#999999";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_BLOCK;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
