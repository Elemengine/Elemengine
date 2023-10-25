package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class SpiritProjectionInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Spirit Projection";
    }

    @Override
    public String getDescription() {
        return "Airbenders who are strongly connected to their spiritual side are able to project their spirit outside of their body.";
    }

    @Override
    public String getChatColor() {
        return "#24bfaf";
    }

    @Override
    public Material getMaterial() {
        return Material.CYAN_CONCRETE_POWDER;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
