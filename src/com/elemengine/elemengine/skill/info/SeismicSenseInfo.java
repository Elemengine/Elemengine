package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class SeismicSenseInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Seismic Sense";
    }

    @Override
    public String getDescription() {
        return "A subskill that augments an earthbender's senses by feeling vibrations through the ground to 'see' where they came from.";
    }

    @Override
    public String getChatColor() {
        return "#c2c080";
    }

    @Override
    public Material getMaterial() {
        return Material.GREEN_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
