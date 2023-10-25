package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class WaterbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Waterbending";
    }

    @Override
    public String getDescription() {
        return "Waterbending is the ability to control water and ice. Water is the element of change, flowing between offense and defense, turning their opponents power against them.";
    }

    @Override
    public String getChatColor() {
        return "#0074d9";
    }

    @Override
    public Material getMaterial() {
        return Material.BLUE_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
