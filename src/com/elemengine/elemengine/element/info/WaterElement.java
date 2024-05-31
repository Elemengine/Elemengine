package com.elemengine.elemengine.element.info;

import java.util.Arrays;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class WaterElement implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Water";
    }

    @Override
    public String getDescription() {
        return "Water is the element of change, being adaptable and turning their opponent's power against them.";
    }

    @Override
    public String getChatColor() {
        return "#0074d9";
    }

    @Override
    public Material getMaterial() {
        return Material.BLUE_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        config.addDefault("waterlogged", true);
        
        Material[] bendable = new Material[] {
            Material.WATER, Material.WATER_CAULDRON, Material.SNOW, Material.SNOW_BLOCK, Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE,
        };
        
        config.addDefault("bendables", Arrays.stream(bendable).map(Object::toString).toList());
    }

}
