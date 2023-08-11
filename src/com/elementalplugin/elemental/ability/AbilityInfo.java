package com.elementalplugin.elemental.ability;

import org.bukkit.event.Listener;

import com.elementalplugin.elemental.ability.activation.Trigger;
import com.elementalplugin.elemental.ability.util.Cooldown;
import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.skill.Skills;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.storage.Configurable;

import net.md_5.bungee.api.ChatColor;

public abstract class AbilityInfo implements Configurable, Listener {
    
    protected static final String LEFT_CLICK = Trigger.ID_LEFT_CLICK;
    protected static final String RIGHT_CLICK_BLOCK = Trigger.ID_RIGHT_CLICK_BLOCK;
    protected static final String RIGHT_CLICK_ENTITY = Trigger.ID_RIGHT_CLICK_ENTITY;
    protected static final String SNEAK_DOWN = Trigger.ID_SNEAK_DOWN;
    protected static final String SNEAK_UP = Trigger.ID_SNEAK_UP;
    protected static final String SPRINT_ON = Trigger.ID_SPRINT_ON;
    protected static final String SPRINT_OFF = Trigger.ID_SPRINT_OFF;
    protected static final String DAMAGED = Trigger.ID_DAMAGED;
    protected static final String PASSIVE = Trigger.ID_PASSIVE;
    protected static final String COMBO = Trigger.ID_COMBO;

    private String name, author, version, description;
    private Skill skill;

    public AbilityInfo(String name, String description, String author, String version, String skill) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.skill = Skills.manager().get(skill);
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getAuthor() {
        return author;
    }

    public final String getVersion() {
        return version;
    }

    public final Skill getSkill() {
        return skill;
    }

    public ChatColor getDisplayColor() {
        return skill.getColor();
    }

    public String getDisplay() {
        return getDisplayColor() + name;
    }

    public Cooldown.Tag getCooldownTag() {
        return Cooldown.tag(name, name, this.getDisplayColor(), true);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getFolderName() {
        return skill.getName();
    }

    @Override
    public void postProcessed(Config config) {}

    public boolean hasPassive() {
        return false;
    }

    protected void onRegister() {}

    public boolean canActivate(AbilityUser user, Trigger trigger) {
        return user.hasSkill(skill);
    }
}
