package com.elemengine.elemengine.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.elemengine.elemengine.skill.info.AirbendingInfo;
import com.elemengine.elemengine.skill.info.BloodbendingInfo;
import com.elemengine.elemengine.skill.info.ChiblockingInfo;
import com.elemengine.elemengine.skill.info.CombustionbendingInfo;
import com.elemengine.elemengine.skill.info.EarthbendingInfo;
import com.elemengine.elemengine.skill.info.EnergybendingInfo;
import com.elemengine.elemengine.skill.info.FirebendingInfo;
import com.elemengine.elemengine.skill.info.FlightInfo;
import com.elemengine.elemengine.skill.info.LavabendingInfo;
import com.elemengine.elemengine.skill.info.LightningbendingInfo;
import com.elemengine.elemengine.skill.info.MetalbendingInfo;
import com.elemengine.elemengine.skill.info.PhysiqueInfo;
import com.elemengine.elemengine.skill.info.PlantbendingInfo;
import com.elemengine.elemengine.skill.info.SeismicSenseInfo;
import com.elemengine.elemengine.skill.info.SpiritProjectionInfo;
import com.elemengine.elemengine.skill.info.SpiritWatersInfo;
import com.elemengine.elemengine.skill.info.WaterbendingInfo;
import com.elemengine.elemengine.storage.Config;
import com.elemengine.elemengine.storage.Configurable;
import com.elemengine.elemengine.storage.Configure;
import com.elemengine.elemengine.util.Items;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.ChatColor;

public enum Skill implements Configurable {

    //base skills
    ENERGYBENDING(new EnergybendingInfo()), 
    AIRBENDING(new AirbendingInfo()), 
    EARTHBENDING(new EarthbendingInfo()), 
    FIREBENDING(new FirebendingInfo()), 
    WATERBENDING(new WaterbendingInfo()), 
    CHIBLOCKING(new ChiblockingInfo()),
    
    //airbending subs
    SPIRIT_PROJECTION(new SpiritProjectionInfo(), AIRBENDING), 
    //SOUNDBENDING("Soundbending", Material.YELLOW_CONCRETE_POWDER, "#f5f17f", "A common subskill, airbenders can manipulate sounds by either bending air through instruments or vibrating the air itself.", AIRBENDING),
    FLIGHT(new FlightInfo(), AIRBENDING), 
    
    //earthbending subs
    METALBENDING(new MetalbendingInfo(), EARTHBENDING),
    SEISMIC_SENSE(new SeismicSenseInfo(), EARTHBENDING), 
    LAVABENDING(new LavabendingInfo(), EARTHBENDING), 
    
    //firebending subs
    LIGHTNINGBENDING(new LightningbendingInfo(), FIREBENDING), 
    COMBUSTIONBENDING(new CombustionbendingInfo(), FIREBENDING),
    //BLUE_FIRE("Blue Fire", Material.CYAN_CONCRETE_POWDER, "#42e3f5", "A rare subskill, being able to create hotter pure blue flames, often a sign of prodigious talent.", FIREBENDING),
    
    //waterbending subs
    SPIRIT_WATERS(new SpiritWatersInfo(), WATERBENDING), 
    BLOODBENDING(new BloodbendingInfo(), WATERBENDING), 
    PLANTBENDING(new PlantbendingInfo(), WATERBENDING),
    
    //others
    PHYSIQUE(new PhysiqueInfo(), AIRBENDING, EARTHBENDING, FIREBENDING, WATERBENDING, CHIBLOCKING);
    
    @Configure("item.material") private String configMaterial;
    @Configure("item.customModelData") private int customModelData = -1;
    @Configure("color") private String configColor;
    @Configure private String description;
    
    private SkillInfo info;
    private ImmutableSet<Skill> parents, children;
    private Material type = Material.BARRIER;
    private ChatColor chatColor = ChatColor.WHITE;  
    
    private Skill(SkillInfo info, Skill...parents) { 
        for (Skill parent : parents) {
            if (parent == null) {
                continue;
            }

            parent.children = new ImmutableSet.Builder<Skill>().addAll(parent.children).add(this).build();
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
            meta.setDisplayName(chatColor + info.getDisplayName());
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
        
        info.setupConfig(config);
        config.save();
    }
    
    public static Stream<Skill> streamValues() {
        return Arrays.asList(Skill.values()).stream();
    }
    
    public static Skill from(String name) {
        return Skill.valueOf(name.toUpperCase());
    }
}
