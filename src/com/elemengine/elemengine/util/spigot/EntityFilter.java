package com.elemengine.elemengine.util.spigot;

import java.util.function.Predicate;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.elemengine.elemengine.ability.AbilityUser;

public interface EntityFilter extends Predicate<Entity> {
    
    static EntityFilter notUser(AbilityUser user) {
        return e -> !user.getUniqueID().equals(e.getUniqueId());
    }
    
    static EntityFilter NOT_DISPLAY = e -> e.getType() != EntityType.BLOCK_DISPLAY && e.getType() != EntityType.ITEM_DISPLAY && e.getType() != EntityType.TEXT_DISPLAY;
}
