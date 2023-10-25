package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class PlantbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Plantbending";
    }

    @Override
    public String getDescription() {
        return "Plantbending is used to pull water from plants or to control plants themselves in place of water, being more solid than water but more flexible than ice.";
    }

    @Override
    public String getChatColor() {
        return "#1bb374";
    }

    @Override
    public Material getMaterial() {
        return Material.GREEN_GLAZED_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
