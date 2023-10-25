package com.elemengine.elemengine.skill.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;

import com.elemengine.elemengine.ability.AbilityUser;
import com.google.common.base.Preconditions;

public interface FirebendingUtils {
    
    public static final double[] ZEROES = {0, 0, 0};
    
    public static final double FLAME_SPEED = 0.0126;
    
    public static final DustTransition SPARK_DUST = new DustTransition(Color.fromRGB(126, 252, 242), Color.fromRGB(250, 250, 250), 0.9f);

    public default void spawnFlame(AbilityUser user, double[] position, int amount, double[] offsets, double speed) {
        Preconditions.checkArgument(position.length == 3, "Flame spawn position requires 3 values");
        Preconditions.checkArgument(offsets.length == 3, "Flame spawn offsets requires 3 values");
        
        Particle flame = user.hasPermission("elemengine.firebending.blueflames") ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        user.getWorld().spawnParticle(flame, position[0], position[1], position[2], amount, offsets[0], offsets[1], offsets[2], speed, null);
    }
    
    public default void spawnFlame(AbilityUser user, double[] position, int amount, double[] offsets) {
        spawnFlame(user, position, amount, offsets, FLAME_SPEED);
    }
    
    public default void spawnFlame(AbilityUser user, double[] position, int amount, double speed) {
        spawnFlame(user, position, amount, ZEROES, speed);
    }
    
    public default void spawnFlame(AbilityUser user, double[] position, int amount) {
        spawnFlame(user, position, amount, ZEROES, FLAME_SPEED);
    }
    
    public default void spawnFlame(AbilityUser user, double[] position) {
        spawnFlame(user, position, 1, ZEROES, FLAME_SPEED);
    }
    
    public default void spawnFlame(AbilityUser user, Location loc, int amount, double[] offsets, double speed) {
        Preconditions.checkArgument(offsets.length == 3, "Flame spawn offsets requires 3 values");
        
        Particle flame = user.hasPermission("elemengine.firebending.blueflames") ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        user.getWorld().spawnParticle(flame, loc, amount, offsets[0], offsets[1], offsets[2], speed, null);
    }
    
    public default void spawnFlame(AbilityUser user, Location loc, int amount, double[] offsets) {
        spawnFlame(user, loc, amount, offsets, FLAME_SPEED);
    }
    
    public default void spawnFlame(AbilityUser user, Location loc, int amount, double speed) {
        spawnFlame(user, loc, amount, ZEROES, speed);
    }
    
    public default void spawnFlame(AbilityUser user, Location loc, int amount) {
        spawnFlame(user, loc, amount, ZEROES, FLAME_SPEED);
    }
    
    public default void spawnFlame(AbilityUser user, Location loc) {
        spawnFlame(user, loc, 1, ZEROES, FLAME_SPEED);
    }
    
    public default void spawnSpark(AbilityUser user, Location loc, int amount, double[] offsets, double speed) {
        Preconditions.checkArgument(offsets.length == 3, "Spark spawn offsets requires 3 values");
        
        user.getWorld().spawnParticle(Particle.REDSTONE, loc, amount, offsets[0], offsets[1], offsets[2], speed, SPARK_DUST);
    }
    
    public default void spawnSpark(AbilityUser user, Location loc, int amount, double[] offsets) {
        spawnSpark(user, loc, amount, offsets, 0);
    }
    
    public default void spawnSpark(AbilityUser user, Location loc, int amount, double speed) {
        spawnSpark(user, loc, amount, ZEROES, speed);
    }
    
    public default void spawnSpark(AbilityUser user, Location loc, int amount) {
        spawnSpark(user, loc, amount, ZEROES, 0);
    }
    
    public default void spawnSpark(AbilityUser user, Location loc) {
        spawnSpark(user, loc, 1, ZEROES, 0);
    }
    
    public default void spawnSpark(AbilityUser user, double[] position, int amount, double[] offsets, double speed) {
        Preconditions.checkArgument(offsets.length == 3, "Spark spawn offsets requires 3 values");
        
        user.getWorld().spawnParticle(Particle.REDSTONE, position[0], position[1], position[2], amount, offsets[0], offsets[1], offsets[2], speed, SPARK_DUST);
    }
    
    public default void spawnSpark(AbilityUser user, double[] position, int amount, double[] offsets) {
        spawnSpark(user, position, amount, offsets, 0);
    }
    
    public default void spawnSpark(AbilityUser user, double[] position, int amount, double speed) {
        spawnSpark(user, position, amount, ZEROES, speed);
    }
    
    public default void spawnSpark(AbilityUser user, double[] position, int amount) {
        spawnSpark(user, position, amount, ZEROES, 0);
    }
    
    public default void spawnSpark(AbilityUser user, double[] position) {
        spawnSpark(user, position, 1, ZEROES, 0);
    }
}
