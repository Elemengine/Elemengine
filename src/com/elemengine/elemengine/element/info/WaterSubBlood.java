package com.elemengine.elemengine.element.info;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class WaterSubBlood implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Blood";
    }

    @Override
    public String getDescription() {
        return "A dangerous and illegal subelement, being able to bend the water within living creatures to control them. This can usually only be done under a full moon.";
    }

    @Override
    public String getChatColor() {
        return "#630023";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_GLAZED_TERRACOTTA;
    }

    @Override
    public void setupConfig(Config config) {
        EntityType[] blacklist = new EntityType[] {
            EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ARMOR_STAND, EntityType.BLAZE,
            EntityType.BREEZE, EntityType.WARDEN, EntityType.VEX, EntityType.ALLAY, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON,
            EntityType.GHAST, EntityType.SHULKER, EntityType.PHANTOM
        };
        
        config.addDefault("entityBlacklist", Arrays.stream(blacklist).map(Object::toString).toList());
        config.addDefault("onlyFullmoon", true);
    }

}
