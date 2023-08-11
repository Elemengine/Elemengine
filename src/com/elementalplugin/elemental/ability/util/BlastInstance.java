package com.elementalplugin.elemental.ability.util;

import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elementalplugin.elemental.ability.AbilityInfo;
import com.elementalplugin.elemental.ability.AbilityInstance;
import com.elementalplugin.elemental.ability.AbilityUser;

public abstract class BlastInstance extends AbilityInstance {

    private Predicate<Location> passable;
    protected Vector direction;
    protected Location location;
    protected double speed;

    public BlastInstance(AbilityInfo ability, AbilityUser user, Predicate<Location> passable) {
        super(ability, user);
        this.passable = passable;
        this.direction = user.getDirection();
        this.location = user.getEyeLocation();
    }

    @Override
    protected boolean onUpdate(double timeDelta) {
        location.add(direction.clone().multiply(speed * timeDelta));
        return passable.test(location);
    }

    /**
     * Sets the speed of this blast
     * 
     * @param speed blocks per second
     */
    public final void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the direction of this blast without changing the speed
     * 
     * @param direction blast's new direction
     */
    public final void setDirection(Vector direction) {
        this.direction.copy(direction);
        if (this.direction.lengthSquared() > Vector.getEpsilon()) {
            this.direction.normalize();
        }
    }

    public final Vector getDirection() {
        return this.direction.clone();
    }

    public final Location getLocation() {
        return this.location;
    }

    /**
     * Turns this blast towards the given direction without changing the speed. The
     * length of the given direction will determine how much the blast turns toward
     * it (longer = more turning)
     * 
     * @param direction where to turn towards
     */
    public final void turnTowards(Vector direction) {
        this.direction.add(direction).normalize();
    }

    public final RayTraceResult rayTrace(double distance, FluidCollisionMode fcm, boolean ignorePassables, double raySize) {
        return location.getWorld().rayTrace(location, direction, distance, fcm, ignorePassables, raySize, null);
    }

    public final RayTraceResult rayTrace(double distance, FluidCollisionMode fcm, boolean ignorePassables, double raySize, Predicate<Entity> filter) {
        return location.getWorld().rayTrace(location, direction, distance, fcm, ignorePassables, raySize, filter);
    }
}
