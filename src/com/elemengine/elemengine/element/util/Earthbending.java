package com.elemengine.elemengine.element.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;

public final class Earthbending {
    
    public static final Config EARTH_CONFIG = Config.from("_properties", Element.EARTH.getFolderName());
    public static final Config METAL_CONFIG = Config.from("_properties", Element.METAL.getFolderName());
    public static final Config LAVA_CONFIG = Config.from("_properties", Element.LAVA.getFolderName());
    
    private Earthbending() {}
    
    public static boolean canEarthbend(AbilityUser user, Material mat) {
        return (user.hasElement(Element.EARTH) && isEarthbendable(mat))
            || (user.hasElement(Element.METAL) && isMetalbendable(mat))
            || (user.hasElement(Element.LAVA)  && isLavabendable(mat));
    }
    
    public static boolean canEarthbend(AbilityUser user, Block block) {
        return canEarthbend(user, block.getType());
    }
    
    public static boolean canEarthbend(AbilityUser user, Location loc) {
        return canEarthbend(user, loc.getBlock().getType());
    }

    public static boolean isEarthbendable(Material material) {
        return EARTH_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public static boolean isEarthbendable(Block block) {
        return isEarthbendable(block.getType());
    }
    
    public static boolean isEarthbendable(Location loc) {
        return isEarthbendable(loc.getBlock().getType());
    }
    
    public static boolean isMetalbendable(Material material) {
        return METAL_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public static boolean isMetalbendable(Block block) {
        return isMetalbendable(block.getType());
    }
    
    public static boolean isMetalbendable(Location loc) {
        return isMetalbendable(loc.getBlock().getType());
    }
    
    public static boolean isLavabendable(Material material) {
        return LAVA_CONFIG.get(FileConfiguration::getList, "bendableBlocks").contains(material.toString());
    }
    
    public static boolean isLavabendable(Block block) {
        return isLavabendable(block.getType());
    }
    
    public static boolean isLavabendable(Location loc) {
        return isLavabendable(loc.getBlock().getType());
    }
}
