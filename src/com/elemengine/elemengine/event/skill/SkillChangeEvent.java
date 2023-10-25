package com.elemengine.elemengine.event.skill;

import java.util.Collection;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.skill.SkillHolder;
import com.google.common.collect.ImmutableSet;

public class SkillChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private SkillHolder holder;
    private Set<Skill> skills;

    public SkillChangeEvent(SkillHolder holder, Collection<Skill> skills) {
        this.holder = holder;
        this.skills = new ImmutableSet.Builder<Skill>().addAll(skills).build();
    }

    public SkillHolder getHolder() {
        return holder;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
