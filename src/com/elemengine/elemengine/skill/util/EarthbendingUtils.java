package com.elemengine.elemengine.skill.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.storage.Config;

public interface EarthbendingUtils {
    
    public static final Config EARTH_CONFIG = Config.from("_properties", "earthbending");
    public static final Config METAL_CONFIG = Config.from("_properties", "metalbending");
    public static final Config LAVA_CONFIG = Config.from("_properties", "lavabending");
    
    public default boolean canEarthbend(AbilityUser user, Material mat) {
        return (user.hasSkill(Skill.EARTHBENDING) && isEarthbendable(mat))
            || (user.hasSkill(Skill.METALBENDING) && isMetalbendable(mat))
            || (user.hasSkill(Skill.LAVABENDING)  && isLavabendable(mat));
    }
    
    public default boolean canEarthbend(AbilityUser user, Block block) {
        return canEarthbend(user, block.getType());
    }
    
    public default boolean canEarthbend(AbilityUser user, Location loc) {
        return canEarthbend(user, loc.getBlock().getType());
    }

    public default boolean isEarthbendable(Material material) {
        return EARTH_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public default boolean isEarthbendable(Block block) {
        return isEarthbendable(block.getType());
    }
    
    public default boolean isEarthbendable(Location loc) {
        return isEarthbendable(loc.getBlock().getType());
    }
    
    public default boolean isMetalbendable(Material material) {
        return METAL_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public default boolean isMetalbendable(Block block) {
        return isMetalbendable(block.getType());
    }
    
    public default boolean isMetalbendable(Location loc) {
        return isMetalbendable(loc.getBlock().getType());
    }
    
    public default boolean isLavabendable(Material material) {
        return LAVA_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public default boolean isLavabendable(Block block) {
        return isLavabendable(block.getType());
    }
    
    public default boolean isLavabendable(Location loc) {
        return isLavabendable(loc.getBlock().getType());
    }
}
