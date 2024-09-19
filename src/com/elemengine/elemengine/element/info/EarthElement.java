package com.elemengine.elemengine.element.info;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.elemengine.elemengine.element.ElementInfo;
import com.elemengine.elemengine.storage.configuration.Config;

public class EarthElement implements ElementInfo {

    @Override
    public String getDisplayName() {
        return "Earth";
    }

    @Override
    public String getDescription() {
        return "Earth is the element of substance, enduring attacks until they have an opportunity to strike back.";
    }

    @Override
    public String getChatColor() {
        return "#0cb153";
    }

    @Override
    public Material getMaterial() {
        return Material.LIME_CONCRETE;
    }

    @Override
    public void setupConfig(Config config) {
        List<String> bendable = new ArrayList<>();
        
        for (Material mat : Material.values()) {
            if (mat.isSolid() && !mat.isInteractable() && isEarthen(mat)) bendable.add(mat.toString());
        }
        
        config.addDefault("bendableBlocks", bendable);
    }
    
    private boolean isEarthen(Material mat) {
        return mat.toString().contains("DIRT")       || mat.toString().contains("MUD")
            || mat.toString().contains("GRAVEL")     || mat.toString().contains("SAND")
            || mat.toString().contains("STONE")      || mat.toString().contains("BRICK")
            || mat.toString().contains("DEEPSLATE")  || mat.toString().contains("CONCRETE")
            || mat.toString().contains("TERRACOTTA") || mat.toString().contains("ORE")
            || mat.toString().contains("AMETHYST")   || mat.toString().contains("GRANITE")
            || mat.toString().contains("DIORITE")    || mat.toString().contains("ANDESITE")
            || mat.toString().contains("DIAMOND")    || mat.toString().contains("COAL")     
            || mat.toString().contains("LAPIS")      || mat.toString().contains("EMERALD")
            || mat.toString().contains("QUARTZ")     || mat.toString().contains("BASALT")
            || mat.toString().contains("PRISMARINE") || mat.toString().contains("NETHERRACK")
            || mat.toString().contains("CLAY")       || mat.toString().contains("CALCITE")
            || mat.toString().contains("TUFF")       || mat.toString().contains("LIUM")
            || mat.toString().contains("OBSIDIAN")   || mat.toString().contains("SOIL")
            || mat == Material.PODZOL || mat == Material.FARMLAND;
    }
}
