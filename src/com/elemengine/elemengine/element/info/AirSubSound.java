package com.elemengine.elemengine.element.info;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

import org.bukkit.Material;

public class AirSubSound implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Sound";
    }

    @Override
    public String getDescription() {
        return "Manipulating soundwaves";
    }

    @Override
    public String getChatColor() {
        return "#e7e710";
    }

    @Override
    public Material getMaterial() {
        return Material.NOTE_BLOCK;
    }

    @Override
    public void setupConfig(Config config) {

    }
}
