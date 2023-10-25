package com.elemengine.elemengine.skill.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.storage.Config;

public interface WaterbendingUtil {

    public static final Config WATER_CONFIG = Config.from("_properties", "waterbending");
    public static final Config PLANT_CONFIG = Config.from("_properties", "plantbending");
    public static final Config BLOOD_CONFIG = Config.from("_properties", "bloodbending");
    
    public default boolean canWaterbend(AbilityUser user, Material mat) {
        return (user.hasSkill(Skill.WATERBENDING) && isWaterbendable(mat))
            || (user.hasSkill(Skill.PLANTBENDING) && isPlantbendable(mat));
    }
    
    public default boolean canWaterbend(AbilityUser user, Block block) {
        return canWaterbend(user, block.getType());
    }
    
    public default boolean canWaterbend(AbilityUser user, Location loc) {
        return canWaterbend(user, loc.getBlock().getType());
    }

    public default boolean isWaterbendable(Material material) {
        return WATER_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public default boolean isWaterbendable(Block block) {
        return isWaterbendable(block.getType());
    }
    
    public default boolean isWaterbendable(Location loc) {
        return isWaterbendable(loc.getBlock().getType());
    }
    
    public default boolean isPlantbendable(Material material) {
        return PLANT_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public default boolean isPlantbendable(Block block) {
        return isPlantbendable(block.getType());
    }
    
    public default boolean isPlantbendable(Location loc) {
        return isPlantbendable(loc.getBlock().getType());
    }
    
    public default boolean isBloodbendable(Entity entity) {
        return BLOOD_CONFIG.get(FileConfiguration::getList, "bendableEntities").contains(entity.getType().toString());
    }
}
