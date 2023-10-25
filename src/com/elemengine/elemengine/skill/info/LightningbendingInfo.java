package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class LightningbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Lightningbending";
    }

    @Override
    public String getDescription() {
        return "Once a rare subskill, creating and redirecting lightning has become a common skill among firebenders.";
    }

    @Override
    public String getChatColor() {
        return "#24adbf";
    }

    @Override
    public Material getMaterial() {
        return Material.LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
