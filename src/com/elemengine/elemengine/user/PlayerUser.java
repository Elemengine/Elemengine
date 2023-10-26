package com.elemengine.elemengine.user;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.ability.AbilityUser;

public class PlayerUser extends AbilityUser {

    public PlayerUser(Player player) {
        super(player);
    }
    
    @Override
    public Player getEntity() {
        return (Player) entity;
    }
    
    @Override
    public UUID getUniqueID() {
        return entity.getUniqueId();
    }

    @Override
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public Vector getDirection() {
        return entity.getLocation().getDirection();
    }

    @Override
    public Optional<Entity> getTargetEntity(double range, double raySize, Predicate<Entity> filter) {
        Location eye = getEyeLocation();
        return Optional.ofNullable(eye.getWorld().rayTrace(eye, eye.getDirection(), range, FluidCollisionMode.NEVER, true, raySize, filter)).map(RayTraceResult::getHitEntity);
    }

    @Override
    public int getCurrentSlot() {
        return ((Player) entity).getInventory().getHeldItemSlot();
    }

    @Override
    public void sendMessage(String message) {
        entity.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String perm) {
        return entity.hasPermission(perm);
    }

    @Override
    public boolean shouldRemove() {
        return !((Player) entity).isOnline();
    }

    @Override
    public boolean checkDefaultProtections(Location loc) {
        return loc.getBlock().getType().isSolid();// ? Regions.canBreak(typed, loc) : Regions.canBuild(typed, loc);
    }

    @Override
    public MainHand getMainHand() {
        return ((Player) entity).getMainHand();
    }

    @Override
    public boolean isOnline() {
        return ((Player) entity).isOnline();
    }
    
    
}
