package com.elemengine.elemengine.element;

import org.bukkit.Material;

import com.elemengine.elemengine.storage.configuration.Config;

public interface ElementInfo {

    String getDisplayName();
    String getDescription();
    String getChatColor();
    Material getMaterial();
    void setupConfig(Config config);

}
