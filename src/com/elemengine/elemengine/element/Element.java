package com.elemengine.elemengine.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.elemengine.elemengine.element.info.AirElement;
import com.elemengine.elemengine.element.info.AirSubSound;
import com.elemengine.elemengine.element.info.EarthElement;
import com.elemengine.elemengine.element.info.EarthSubLava;
import com.elemengine.elemengine.element.info.EarthSubMetal;
import com.elemengine.elemengine.element.info.EnergyElement;
import com.elemengine.elemengine.element.info.FireElement;
import com.elemengine.elemengine.element.info.FireSubHeat;
import com.elemengine.elemengine.element.info.FireSubLightning;
import com.elemengine.elemengine.element.info.WaterElement;
import com.elemengine.elemengine.element.info.WaterSubBlood;
import com.elemengine.elemengine.element.info.WaterSubPlant;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.util.Strings;
import com.elemengine.elemengine.util.spigot.Items;
import com.google.common.collect.ImmutableSet;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class Element implements Configurable {
    
    private static Element[] values = new Element[0];

    public static final Element ENERGY      = new Element(new EnergyElement());
    public static final Element AIR         = new Element(new AirElement());
    public static final Element EARTH       = new Element(new EarthElement());
    public static final Element FIRE        = new Element(new FireElement());
    public static final Element WATER       = new Element(new WaterElement());

    public static final Element SOUND       = new Element(new AirSubSound(), AIR);

    public static final Element METAL       = new Element(new EarthSubMetal(), EARTH);
    public static final Element LAVA        = new Element(new EarthSubLava(), EARTH);

    public static final Element LIGHTNING   = new Element(new FireSubLightning(), FIRE);
    public static final Element HEAT        = new Element(new FireSubHeat(), FIRE);

    public static final Element BLOOD       = new Element(new WaterSubBlood(), WATER);
    public static final Element PLANT       = new Element(new WaterSubPlant(), WATER);
    
    @Configure("item.material") private String configMaterial;
    @Configure("item.customModelData") private int customModelData = -1;
    @Configure("color") private String configColor;
    @Configure private String description;
    
    private final int index;
    private final ElementInfo info;
    private final ImmutableSet<Element> parents;
    private ImmutableSet<Element> children;
    private Material type = Material.BARRIER;
    private TextColor chatColor = NamedTextColor.WHITE;  
    
    private Element(ElementInfo info, Element...parents) {
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
        
        this.index = values.length;
        values = Arrays.copyOf(values, index + 1);
        values[index] = this;
        
        Config.process(this);
    }
    
    public Component createComponent() {
        return Component.text(info.getDisplayName()).color(chatColor);
    }
    
    public String getDisplayName() {
        return info.getDisplayName();
    }
    
    public TextColor getChatColor() {
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
            Strings.wrapAnd(this.description, 28, loreDescription::add);
            
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
            this.chatColor = TextColor.fromHexString(configColor.toUpperCase());
        } catch (Exception e) {
            this.chatColor = NamedTextColor.WHITE;
        }
        
        info.setupConfig(config);
        config.save();
    }
    
    @Override
    public String toString() {
        return info.getDisplayName();
    }
    
    public int ordinal() {
        return index;
    }
    
    public static Element[] values() {
        return Arrays.copyOf(values, values.length);
    }
    
    public static Stream<Element> streamValues() {
        return Arrays.stream(Element.values());
    }
    
    /**
     * Attempts to find the Element associated with the given name, regardless of casing.
     * 
     * @param name Name of the element to look for
     * @return null if not found
     */
    public static Element from(String name) {
        for (int i = 0; i < values.length; ++i) {
            Element e = values[i];
            if (e.getDisplayName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        
        return null;
    }
    
    public static Element register(ElementInfo info, Element...parents) {
        Element found = from(info.getDisplayName());
        if (found != null) {
            return null;
        }
        
        return new Element(info, parents);
    }
}
