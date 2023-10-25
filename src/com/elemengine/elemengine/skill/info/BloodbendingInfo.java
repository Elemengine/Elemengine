package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class BloodbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Bloodbending";
    }

    @Override
    public String getDescription() {
        return "A dangerous and illegal subskill, being able to bend the water within living creatures to control them. This can usually only be done under a full moon.";
    }

    @Override
    public String getChatColor() {
        return "#630023";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_GLAZED_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
