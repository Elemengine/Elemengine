package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class MetalbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Metalbending";
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
