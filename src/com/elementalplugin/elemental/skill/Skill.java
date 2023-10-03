package com.elementalplugin.elemental.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.storage.Configurable;
import com.elementalplugin.elemental.storage.Configure;
import com.elementalplugin.elemental.util.Items;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.ChatColor;

public enum Skill implements Configurable {

    //base skills
    ENERGYBENDING("Energybending", Material.PURPLE_GLAZED_TERRACOTTA, "#8f24f2", "Energybending is the ability to control chi. Chi is the energy that exists within all living beings, and what allows them to use bending."), 
    AIRBENDING("Airbending", Material.YELLOW_CONCRETE, "#f7e32e", "Airbending is the ability to control the air and wind. Air is the element of freedom, using mostly defensive or indirect attacks and many, many evasive maneuvers."), 
    EARTHBENDING("Earthbending", Material.LIME_CONCRETE, "#0cb153", "Earthbending is the ability to control the ground and rocks. Earth is the element of substance, enduring attacks until they have an opportunity to counterattack."), 
    FIREBENDING("Firebending", Material.RED_CONCRETE_POWDER, "#f42a10", "Firebending is the ability to create and control fire. Fire is the element of power, preferring to attack aggresively and overpower their opponents."), 
    WATERBENDING("Waterbending", Material.BLUE_GLAZED_TERRACOTTA, "#0074d9", "Waterbending is the ability to control water and ice. Water is the element of change, flowing between offense and defense, turning their opponents power against them."), 
    CHIBLOCKING("Chiblocking", Material.GRAY_CONCRETE_POWDER, "#777777", "Chiblocking is a martial arts designed by those without bending to fight it. The attacks consist of precise strikes to disrupt the flow of chi and inhibit bending."),
    
    //airbending subs
    SPIRIT_PROJECTION("Spirit Projection", Material.CYAN_CONCRETE, "#24bfaf", "Airbenders who have an extremely strong connection to spirits are able to project their spirit outside of their body. ", AIRBENDING), 
    SOUNDBENDING("Soundbending", Material.YELLOW_CONCRETE_POWDER, "#f5f17f", "A common subskill, airbenders can manipulate sounds by either bending air through instruments or vibrating the air itself.", AIRBENDING),
    FLIGHT("Flight", Material.YELLOW_TERRACOTTA, "#f5dd7f", "A rare subskill, airbenders who break away from all of their earthly attachments become untethered by gravity and can freely fly.", AIRBENDING), 
    
    //earthbending subs
    METALBENDING("Metalbending", Material.IRON_BLOCK, "#999999", "Metalbenders are able to control most metals by bending the small pieces of earth within them.", EARTHBENDING),
    SEISMIC_SENSE("Seismic Sense", Material.LIME_TERRACOTTA, "#c2c080", "A subskill that augments an earthbender's senses by feeling vibrations through the ground to 'see' where they came from.", EARTHBENDING), 
    LAVABENDING("Lavabending", Material.ORANGE_CONCRETE, "#bf6224", "A rare subskill, lavabenders can phase shift between normal earth and lava, and control it for much more dangerous attacks. ", EARTHBENDING), 
    
    //firebending subs
    LIGHTNINGBENDING("Lightningbending", Material.LIGHT_BLUE_CONCRETE, "#24adbf", "A once rare subskill, creating and redirecting lightning has become a common skill among firebenders.", FIREBENDING), 
    COMBUSTIONBENDING("Combustionbending", Material.RED_TERRACOTTA, "#ad3434", "A rare subskill, combustionbenders focus their chi through a tattoo on their forehead to generate beams of pure combustive heat.", FIREBENDING), 
    BLUE_FIRE("Blue Fire", Material.CYAN_CONCRETE_POWDER, "#42e3f5", "A rare subskill, being able to create hotter pure blue flames, often a sign of prodigious talent.", FIREBENDING),
    
    //waterbending subs
    SPIRIT_WATERS("Spirit Waters", Material.LIGHT_BLUE_CONCRETE_POWDER, "#59ffe3", "A rare subskill, being able to turn water into spirit water that they can use to heal or read the flow of chi in people.", WATERBENDING), 
    BLOODBENDING("Bloodbending", Material.RED_GLAZED_TERRACOTTA, "#630023", "A dangerous and illegal subskill, being able to bend the water within living creatures to control them. This can usually only be done under a full moon.", WATERBENDING), 
    PLANTBENDING("Plantbending", Material.GREEN_CONCRETE_POWDER, "#1bb374", "Plantbending is used to pull water from plants or to control plants themselves in place of water, being more solid than water but more flexible than ice.", WATERBENDING),
    
    //others
    PHYSIQUE("Physique", Material.MAGENTA_CONCRETE_POWDER, "#cf46fb", "Having a strong and healthy body to assist in other skills.", AIRBENDING, EARTHBENDING, FIREBENDING, WATERBENDING, CHIBLOCKING);
    
    @Configure("material") private String configMaterial;
    @Configure("color") private String configColor;
    @Configure private String description;
    @Configure private int customModelData = -1;
    
    private String displayName;
    private ImmutableSet<Skill> parents, children;
    private Material type = Material.BARRIER;
    private ChatColor chatColor = ChatColor.WHITE;  
    
    private Skill(String displayName, Material material, String chatColor, String desc, Skill...parents) { 
        for (Skill parent : parents) {
            if (parent == null) {
                continue;
            }

            parent.children = new ImmutableSet.Builder<Skill>().addAll(parent.children).add(this).build();
        }

        this.parents = ImmutableSet.copyOf(parents);
        this.children = ImmutableSet.of();
        
        this.configMaterial = material.toString();
        this.configColor = chatColor;
        this.description = desc;
        
        Config.process(this);
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColoredName() {
        return chatColor + displayName;
    }
    
    public ChatColor getChatColor() {
        return chatColor;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ImmutableSet<Skill> getParents() {
        return parents;
    }
    
    public ImmutableSet<Skill> getChildren() {
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
            meta.setDisplayName(chatColor + displayName);
            meta.setCustomModelData(customModelData > 0 ? customModelData : null);
        });
    }
    
    public boolean isParent(Skill skill) {
        return parents.contains(skill);
    }
    
    public boolean isChild(Skill skill) {
        return children.contains(skill);
    }

    @Override
    public String getFileName() {
        return "_properties.yml";
    }

    @Override
    public String getFolderName() {
        return this.toString().toLowerCase();
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
    }
    
    public static Stream<Skill> streamValues() {
        return Arrays.asList(Skill.values()).stream();
    }
}
