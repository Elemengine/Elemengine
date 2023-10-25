package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class SpiritWatersInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Spirit Waters";
    }

    @Override
    public String getDescription() {
        return "A rare subskill, being able to turn water into spirit water that they can use to heal or read the flow of chi in people.";
    }

    @Override
    public String getChatColor() {
        return "#59ffe3";
    }

    @Override
    public Material getMaterial() {
        return Material.LIGHT_BLUE_CONCRETE_POWDER;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
