package com.elemengine.elemengine.skill;

import org.bukkit.Material;

import com.elemengine.elemengine.storage.Config;

public interface SkillInfo {

    String getDisplayName();
    String getDescription();
    String getChatColor();
    Material getMaterial();
    void setupConfig(Config config);

}
