package com.elemengine.elemengine.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.elemengine.elemengine.element.info.*;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.storage.configuration.Configure;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.elemengine.elemengine.util.Items;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.ChatColor;

public enum Element implements Configurable {

    ENERGY      (new EnergyElement()),
    AIR         (new AirElement()),
    EARTH       (new EarthElement()),
    FIRE        (new FireElement()),
    WATER       (new WaterElement()),

    SOUND       (new AirSubSound(), AIR),

    METAL       (new EarthSubMetal(), EARTH),
    LAVA        (new EarthSubLava(), EARTH),

    LIGHTNING   (new FireSubLightning(), FIRE),
    HEAT        (new FireSubHeat(), FIRE),

    BLOOD       (new WaterSubBlood(), WATER),
    PLANT       (new WaterSubPlant(), WATER),
    ;
    
    @Configure("item.material") private String configMaterial;
    @Configure("item.customModelData") private int customModelData = -1;
    @Configure("color") private String configColor;
    @Configure private String description;
    
    private final ElementInfo info;
    private final ImmutableSet<Element> parents;
    private ImmutableSet<Element> children;
    private Material type = Material.BARRIER;
    private ChatColor chatColor = ChatColor.WHITE;  
    
    Element(ElementInfo info, Element...parents) {
        for (Element parent : parents) {
            if (parent == null) {
                continue;
            }

            parent.children = new ImmutableSet.Builder<Element>().addAll(parent.children).add(this).build();
        }

        this.info = info;
        this.parents = ImmutableSet.copyOf(parents);
        this.children = ImmutableSet.of();
        this.configMaterial = info.getMaterial().toString();
        this.configColor = info.getChatColor();
        this.description = info.getDescription();
        
        Config.process(this);
    }
    
    public String getDisplayName() {
        return info.getDisplayName();
    }
    
    public String getColoredName() {
        return chatColor + info.getDisplayName();
    }
    
    public ChatColor getChatColor() {
        return chatColor;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ImmutableSet<Element> getParents() {
        return parents;
    }
    
    public ImmutableSet<Element> getChildren() {
        return children;
    }
    
    public Material getMaterial() {
        return type;
    }
    
    public ItemStack newItemStack() {
        return Items.create(type, meta -> {
            List<String> loreDescription = new ArrayList<>();
            String line = "";
            int i = 0;
            for (String s : description.split(" ")) {
                line += s + " ";
                if (++i >= 15) {
                    loreDescription.add(line);
                    line = "";
                }
            }
            
            loreDescription.add(line);
            
            meta.setLore(loreDescription);
            meta.addItemFlags(ItemFlag.values());
            meta.setDisplayName(chatColor + info.getDisplayName());
            meta.setCustomModelData(customModelData > 0 ? customModelData : null);
        });
    }
    
    public boolean hasParent(Element element) {
        return parents.contains(element);
    }
    
    public boolean hasChild(Element element) {
        return children.contains(element);
    }

    @Override
    public String getFileName() {
        return "_properties.yml";
    }

    @Override
    public String getFolderName() {
        return "elements/" + this.toString().toLowerCase();
    }

    @Override
    public void postProcessed(Config config) {
        try {
            this.type = Material.valueOf(configMaterial.toUpperCase());
        } catch (Exception e) {
            this.type = Material.BARRIER;
        }
        
        try {
            this.chatColor = ChatColor.of(configColor.toUpperCase());
        } catch (Exception e) {
            this.chatColor = ChatColor.WHITE;
        }
        
        info.setupConfig(config);
        config.save();
    }
    
    public static Stream<Element> streamValues() {
        return Arrays.stream(Element.values());
    }
    
    public static Element from(String name) {
        return Element.valueOf(name.toUpperCase());
    }
}
