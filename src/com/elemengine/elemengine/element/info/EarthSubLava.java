package com.elemengine.elemengine.element.info;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class EarthSubLava implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Lava";
    }

    @Override
    public String getDescription() {
        return "A rare subelement, lavabenders can phase shift between normal earth and lava, and bend lava for much more dangerous attacks.";
    }

    @Override
    public String getChatColor() {
        return "";
    }

    @Override
    public Material getMaterial() {
        return Material.MAGMA_BLOCK;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
