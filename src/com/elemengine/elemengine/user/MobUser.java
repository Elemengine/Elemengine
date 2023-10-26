package com.elemengine.elemengine.user;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.ability.AbilityUser;

public class MobUser extends AbilityUser {

    private MobSlotAI ai;

    MobUser(Mob entity, MobSlotAI ai) {
        super(entity);
        this.ai = ai;
    }
    
    @Override
    public Mob getEntity() {
        return (Mob) entity;
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
    public boolean shouldRemove() {
        return entity.isDead();
    }

    @Override
    public int getCurrentSlot() {
        return ai.slot();
    }

    @Override
    public void sendMessage(String message) {
        // add config option to silence such messages or log them somewhere else
        Elemengine.plugin().getLogger().info("Message sent to MobUser (" + entity.getUniqueId() + ", " + entity.getCustomName() + "): `" + message + "`");
    }

    @Override
    public boolean hasPermission(String perm) {
        return entity.hasPermission(perm);
    }

    @Override
    public boolean checkDefaultProtections(Location loc) {
        return false;
    }

    @Override
    public MainHand getMainHand() {
        return MainHand.RIGHT;
    }
}
