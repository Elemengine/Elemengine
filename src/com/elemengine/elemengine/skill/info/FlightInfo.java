package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class FlightInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Flight";
    }

    @Override
    public String getDescription() {
        return "A rare subskill, airbenders who break away from all of their earthly attachments become untethered by gravity and can freely fly.";
    }

    @Override
    public String getChatColor() {
        return "#f5dd7f";
    }

    @Override
    public Material getMaterial() {
        return Material.YELLOW_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
