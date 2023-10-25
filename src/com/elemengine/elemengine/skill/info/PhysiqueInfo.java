package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class PhysiqueInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Physique";
    }

    @Override
    public String getDescription() {
        return "Having a strong and healthy body to assist in other skills.";
    }

    @Override
    public String getChatColor() {
        return "#cf46fb";
    }

    @Override
    public Material getMaterial() {
        return Material.MAGENTA_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
