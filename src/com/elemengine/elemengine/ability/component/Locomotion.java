package com.elemengine.elemengine.ability.component;

import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.util.math.rays.RayTraceSettings;

public class Locomotion {
    
    private final Location loc;
    private RayTraceSettings rayTracing;
    
    public Locomotion(Location loc) {
        this.loc = loc.clone();
    }
    
    public Location getLocation() {
        return loc;
    }
    
    public RayTraceResult move(Vector delta) {
        RayTraceResult ray = null;
        
        if (rayTracing != null) {
            ray = rayTracing.fire(loc, delta);
            
            if (ray != null) {
                Vector hit = ray.getHitPosition();
                loc.setX(hit.getX());
                loc.setY(hit.getY());
                loc.setZ(hit.getZ());
            }
        }
        
        if (ray == null) {
            loc.add(delta);
        }
        
        
        return ray;
    }
    
    public Locomotion setRayTracing(RayTraceSettings settings) {
        this.rayTracing = settings;
        return this;
    }
}
