package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class CombustionbendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Combustionbending";
    }

    @Override
    public String getDescription() {
        return "A rare subskill, combustionbenders focus their chi through a tattoo on their forehead to generate beams of pure combustive heat.";
    }

    @Override
    public String getChatColor() {
        return "#ad3434";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
