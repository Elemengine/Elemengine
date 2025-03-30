package com.elemengine.elemengine.element.info;

import java.util.Arrays;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class WaterSubPlant implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Plant";
    }

    @Override
    public String getDescription() {
        return "Plantbending is used to pull water from plants or to control plants themselves in place of water, being more solid than water but more flexible than ice.";
    }

    @Override
    public String getChatColor() {
        return "#1bb374";
    }

    @Override
    public Material getMaterial() {
        return Material.KELP;
    }

    @Override
    public void setupConfig(Config config) {
        Material[] bendable = new Material[] {
            Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES, Material.DARK_OAK_LEAVES, Material.FLOWERING_AZALEA_LEAVES,
            Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.VINE, Material.CAVE_VINES, Material.CAVE_VINES_PLANT,
            Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP,
            Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY
        };
        
        config.addDefault("bendables", Arrays.stream(bendable).map(Object::toString).toList());
    }

}
