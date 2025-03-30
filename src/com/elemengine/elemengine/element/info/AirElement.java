package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class AirElement implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Air";
    }

    @Override
    public String getDescription() {
        return "Air is the element of freedom, using mostly defensive or indirect attacks and evasive techniques.";
    }

    @Override
    public String getChatColor() {
        return "#f7e32e";
    }

    @Override
    public Material getMaterial() {
        return Material.WIND_CHARGE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
