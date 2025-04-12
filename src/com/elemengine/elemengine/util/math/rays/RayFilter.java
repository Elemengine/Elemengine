package com.elemengine.elemengine.util.math.rays;

import java.util.function.Predicate;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.elemengine.elemengine.ability.AbilityUser;

public interface RayFilter extends Predicate<Entity> {
    
    static RayFilter NOT_DISPLAY = e -> e.getType() != EntityType.BLOCK_DISPLAY && e.getType() != EntityType.ITEM_DISPLAY && e.getType() != EntityType.TEXT_DISPLAY;
    
    static RayFilter notUser(AbilityUser user) {
        return e -> !user.getUniqueID().equals(e.getUniqueId());
    }
    
}
