package com.elemengine.elemengine.element.util;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.event.user.UserCheckSourceEvent;
import com.elemengine.elemengine.event.user.UserRequestSourceEvent;
import com.elemengine.elemengine.event.user.UserSupplySourceEvent;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.temporary.Molecule;
import com.elemengine.elemengine.util.Events;
import com.elemengine.elemengine.util.Vectors;

public interface WaterbendingUtils {

    static final Config WATER_CONFIG = Config.from("_properties", Element.WATER.getFolderName());
    static final Config PLANT_CONFIG = Config.from("_properties", Element.PLANT.getFolderName());
    static final Config BLOOD_CONFIG = Config.from("_properties", Element.BLOOD.getFolderName());
    
    static final BlockData FAUX_WATER = Material.BLUE_CONCRETE.createBlockData();
    
    default Molecule spawnWater(AbilityUser user, int amount, double x, double y, double z, float offsets) {
        return spawnWater(user, amount, x, y, z, offsets, new Vector3f(), 0.4f);
    }
    
    default Molecule spawnWater(AbilityUser user, int amount, double x, double y, double z, float offsets, Vector3f drift) {
        return spawnWater(user, amount, x, y, z, offsets, drift, 0.4f);
    }
    
    default Molecule spawnWater(AbilityUser user, int amount, double x, double y, double z, float offsets, Vector3f drift, float scaling) {
        Molecule molecule = new Molecule(user.getWorld(), x, y, z);
        
        for (int i = 0; i < amount; ++i) {
            Vector3f offset = Vectors.random3F().mul(ThreadLocalRandom.current().nextFloat() * offsets);
            molecule.add(FAUX_WATER, scaling, offset, drift);
        }
        
        return molecule;
    }
    
    default UserRequestSourceEvent requestSource(AbilityUser user, int amount) {
        return Events.call(new UserRequestSourceEvent(user, Element.WATER, amount));
    }
    
    default UserSupplySourceEvent supplySource(AbilityUser user, int amount) {
        return Events.call(new UserSupplySourceEvent(user, Element.WATER, amount));
    }
    
    default UserCheckSourceEvent checkSource(AbilityUser user) {
        return Events.call(new UserCheckSourceEvent(user, Element.WATER));
    }
    
    default boolean canWaterbend(AbilityUser user, Material mat) {
        return (user.hasElement(Element.WATER) && isWaterbendable(mat))
            || (user.hasElement(Element.PLANT) && isPlantbendable(mat));
    }
    
    default boolean canWaterbend(AbilityUser user, Block block) {
        return canWaterbend(user, block.getType())
            || (WATER_CONFIG.get(FileConfiguration::getBoolean, "waterlogged") && block.getBlockData() instanceof Waterlogged watered && watered.isWaterlogged());
    }
    
    default boolean canWaterbend(AbilityUser user, Location loc) {
        return canWaterbend(user, loc.getBlock().getType());
    }

    default boolean isWaterbendable(Material material) {
        return WATER_CONFIG.get(FileConfiguration::getList, "bendables").contains(material.toString());
    }
    
    default boolean isWaterbendable(Block block) {
        return isWaterbendable(block.getType())
            || (WATER_CONFIG.get(FileConfiguration::getBoolean, "waterlogged") && block.getBlockData() instanceof Waterlogged watered && watered.isWaterlogged());
    }
    
    default boolean isWaterbendable(Location loc) {
        return isWaterbendable(loc.getBlock().getType());
    }
    
    default boolean isPlantbendable(Material material) {
        return PLANT_CONFIG.get(FileConfiguration::getList, "bendables").contains(material.toString());
    }
    
    default boolean isPlantbendable(Block block) {
        return isPlantbendable(block.getType());
    }
    
    default boolean isPlantbendable(Location loc) {
        return isPlantbendable(loc.getBlock().getType());
    }
    
    default boolean isBloodbendable(Entity entity) {
        return !BLOOD_CONFIG.get(FileConfiguration::getList, "entityBlacklist").contains(entity.getType().toString());
    }
}
