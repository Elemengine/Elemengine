package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class EnergybendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Energybending";
    }

    @Override
    public String getDescription() {
        return "Energybending is the ability to control chi. Chi is the energy that exists within all living beings, and what allows them to use bending.";
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
