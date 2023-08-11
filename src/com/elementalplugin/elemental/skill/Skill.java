package com.elementalplugin.elemental.skill;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.effect.ParticleData;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.storage.Configurable;
import com.elementalplugin.elemental.storage.Configure;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.ChatColor;

public final class Skill implements Configurable {

    @Configure private String color;
    @Configure private String materialRepresentation;

    private String name, description;
    private ChatColor colored;
    private ParticleData particle;
    ImmutableSet<Skill> parents, children;

    /**
     * A skill is something that entities can do, common examples of this are
     * elemental bending, martial arts, weapon using, and spirit powers.
     * 
     * @param display     {@link DisplayVariant} for this skill
     * @param description What this skill enables players to do
     */
    public Skill(String name, String color, String description, Material itemized, ParticleData particle) {
        this.name = name.toLowerCase();
        this.color = color;
        this.description = description;
        this.materialRepresentation = itemized.toString();
        this.particle = particle;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ChatColor getColor() {
        return colored;
    }

    public String getColoredName() {
        return colored + name;
    }

    public ImmutableSet<Skill> getChildren() {
        return children;
    }

    public ImmutableSet<Skill> getParents() {
        return parents;
    }

    public ParticleData getParticle() {
        return particle;
    }

    public Material getMaterialRepresentation() {
        return Material.valueOf(materialRepresentation);
    }

    public ItemStack getItemRepresentation() {
        ItemStack item = new ItemStack(this.getMaterialRepresentation());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(this.getColoredName());
        meta.setLore(Arrays.asList(this.description));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getFileName() {
        return "_properties";
    }

    @Override
    public String getFolderName() {
        return name;
    }

    @Override
    public void postProcessed(Config config) {
        try {
            this.colored = ChatColor.of(color);
        } catch (Exception e) {
            Elemental.plugin().getLogger().warning("Invalid chatcolor of '" + color + "' for skill '" + name + "', using white.");
            this.colored = ChatColor.WHITE;
        }
    }
}
