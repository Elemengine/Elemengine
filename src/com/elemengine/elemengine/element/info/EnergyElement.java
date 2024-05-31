package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class EnergyElement implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Energy";
    }

    @Override
    public String getDescription() {
        return "Rather than a physical element to bend, energybending is manipulating the chi that exists in all living beings.";
    }

    @Override
    public String getChatColor() {
        return "#8f24f2";
    }

    @Override
    public Material getMaterial() {
        return Material.PURPLE_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
