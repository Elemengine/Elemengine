package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class ChiblockingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Chiblocking";
    }

    @Override
    public String getDescription() {
        return "Chiblocking is a martial arts designed by those without bending to fight it. The attacks consist of precise strikes to disrupt the flow of chi and inhibit bending.";
    }

    @Override
    public String getChatColor() {
        return "#777777";
    }

    @Override
    public Material getMaterial() {
        return Material.GRAY_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
