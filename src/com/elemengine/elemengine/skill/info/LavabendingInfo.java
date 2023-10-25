package com.elemengine.elemengine.skill.info;

import org.bukkit.Material;

import com.elemengine.elemengine.skill.SkillInfo;
import com.elemengine.elemengine.storage.Config;

public class LavabendingInfo implements SkillInfo {

    @Override
    public String getDisplayName() {
        return "Lavabending";
    }

    @Override
    public String getDescription() {
        return "A rare subskill, lavabenders can phase shift between normal earth and lava, and bend lava for much more dangerous attacks.";
    }

    @Override
    public String getChatColor() {
        return "";
    }

    @Override
    public Material getMaterial() {
        return Material.MAGMA_BLOCK;
    }

    @Override
    public void setupConfig(Config config) {
        
    }

}
