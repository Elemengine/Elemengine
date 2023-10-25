package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class FirebendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Firebending";
    }

    @Override
    public String getDescription() {
        return "Firebending is the ability to create and control fire. Fire is the element of power, preferring to attack aggresively and overpower their opponents.";
    }

    @Override
    public String getChatColor() {
        return "#f42a10";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
