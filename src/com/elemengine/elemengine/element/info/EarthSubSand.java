package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class EarthSubSand implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Sand";
    }

    @Override
    public String getDescription() {
        return "Bending the fine particles of sand allows for techniques that mimic airbending and waterbending, compacting or loosening as needed.";
    }

    @Override
    public String getChatColor() {
        return "#cbbd93";
    }

    @Override
    public Material getMaterial() {
        return Material.SAND;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
