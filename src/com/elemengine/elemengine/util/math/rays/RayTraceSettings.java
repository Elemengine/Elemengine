package com.elemengine.elemengine.util.math.rays;

import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.util.math.Vectors;
import com.elemengine.elemengine.util.spigot.EntityFilter;

public class RayTraceSettings {

    private double length = 0.0;
    private FluidCollisionMode fluids = FluidCollisionMode.NEVER;
    private boolean ignorePassable = true;
    private double size = 1.0;
    private Predicate<Entity> filter = null;
    
    public RayTraceSettings length(double length) {
        this.length = length;
        return this;
    }
    
    public RayTraceSettings fluidCollision(FluidCollisionMode fluids) {
        this.fluids = fluids;
        return this;
    }
    
    public RayTraceSettings ignorePassable(boolean ignorePassable) {
        this.ignorePassable = ignorePassable;
        return this;
    }
    
    public RayTraceSettings size(double size) {
        this.size = size;
        return this;
    }
    
    public RayTraceSettings entityFilter(EntityFilter filter, EntityFilter...extras) {
        this.filter = filter;
        
        int start = 0;
        if (this.filter == null) {
            this.filter = extras[0];
            start = 1;
        }
        
        for (int i = start; i < extras.length; ++i) {
            this.filter = this.filter.and(extras[i]);
        }
        
        return this;
    }
    
    public RayTraceResult fire(Location loc, Vector direction) {
        return Vectors.rayTrace(loc, direction, length <= Vector.getEpsilon() ? direction.length() : length, fluids, ignorePassable, size, filter);
    }
    
    public boolean fire(Location loc, Vector direction, HitEntityCallback hitEntity, HitBlockCallback hitBlock) {
        RayTraceResult result = this.fire(loc, direction);
        
        if (result == null) {
            return false;
        }
        
        if (result.getHitEntity() != null) {
            hitEntity.accept(result.getHitPosition(), result.getHitEntity());
            return true;
        }
        
        hitBlock.accept(result.getHitPosition(), result.getHitBlock(), result.getHitBlockFace());
        return true;
    }
}
