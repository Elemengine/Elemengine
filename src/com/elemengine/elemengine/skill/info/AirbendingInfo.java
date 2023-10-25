package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class AirbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Airbending";
    }

    @Override
    public String getDescription() {
        return "Airbending is the ability to control the air and wind. Air is the element of freedom, using mostly defensive or indirect attacks and many, many evasive maneuvers.";
    }

    @Override
    public String getChatColor() {
        return "#f7e32e";
    }

    @Override
    public Material getMaterial() {
        return Material.YELLOW_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
