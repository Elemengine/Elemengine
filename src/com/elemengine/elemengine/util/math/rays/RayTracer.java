package com.elemengine.elemengine.util.math.rays;

import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.util.math.Vectors;

public class RayTracer {

    private final Vector direction;
    private double length = 1.0;
    private FluidCollisionMode fluids = FluidCollisionMode.NEVER;
    private boolean ignorePassable = true;
    private double size = 1.0;
    private Predicate<Entity> filter = null;
    
    public RayTracer(Vector direction) {
        this.direction = direction.clone();
    }
    
    public RayTracer direction(Vector direction) {
        this.direction.copy(direction);
        return this;
    }
    
    public RayTracer direction(double x, double y, double z) {
        this.direction.setX(x);
        this.direction.setY(y);
        this.direction.setZ(z);
        return this;
    }
    
    public RayTracer length(double length) {
        this.length = length;
        return this;
    }
    
    public RayTracer fluidCollision(FluidCollisionMode fluids) {
        this.fluids = fluids;
        return this;
    }
    
    public RayTracer ignorePassable(boolean ignorePassable) {
        this.ignorePassable = ignorePassable;
        return this;
    }
    
    public RayTracer size(double size) {
        this.size = size;
        return this;
    }
    
    public RayTracer entityFilter(Predicate<Entity> filter) {
        this.filter = filter;
        return this;
    }
    
    public RayTraceResult check(Location loc) {
        return Vectors.rayTrace(loc, direction, length, fluids, ignorePassable, size, filter);
    }
    
    /**
     * Fires the ray from the given location and will run either hit callback when necessary,
     * if at all. The HitEntityCallback will take priority over the HitBlockCallback
     * @param loc Where to fire the ray from
     * @param hitEntity What to do with the hit entity, takes priority over a hit block
     * @param hitBlock What to do with the hit block
     * @return true if the ray hit something, false otherwise
     */
    public boolean check(Location loc, HitEntityCallback hitEntity, HitBlockCallback hitBlock) {
        RayTraceResult result = this.check(loc);
        
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
