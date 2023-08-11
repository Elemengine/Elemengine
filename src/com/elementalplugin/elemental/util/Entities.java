package com.elementalplugin.elemental.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.ability.AbilityInstance;
import com.elementalplugin.elemental.event.ability.InstanceDamageEntityEvent;
import com.elementalplugin.elemental.event.ability.InstanceIgniteEntityEvent;
import com.elementalplugin.elemental.event.ability.InstanceMoveEntityEvent;

public final class Entities {

    private static final String LOCKED = "velocity_locked";
    private static final String METAKEY = "velocity";

    private Entities() {}

    /**
     * Apply damage to the given target from the ability source, with the option to
     * calculate ignore armor
     * 
     * @param target      The entity to damage
     * @param damage      How much damage to apply
     * @param source      The ability causing the damage
     * @param ignoreArmor Whether to ignore armor stats
     */
    public static boolean damage(LivingEntity target, double damage, AbilityInstance source, boolean ignoreArmor) {
        if (target.getNoDamageTicks() > target.getMaximumNoDamageTicks() / 2.0f && damage <= target.getLastDamage()) {
            return false;
        }

        InstanceDamageEntityEvent event = Events.call(new InstanceDamageEntityEvent(target, damage, source, ignoreArmor));
        if (event.isCancelled()) {
            return false;
        }

        damage = event.getDamage();

        if (event.doesIgnoreArmor() && damage > 0) {
            double defense = target.getAttribute(Attribute.GENERIC_ARMOR).getValue();
            double toughness = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            damage /= 1 - (Math.min(20, Math.max(defense / 5, defense - 4 * damage / (toughness + 8)))) / 25;
        }

        target.damage(damage, source.getUser().getEntity());
        return true;
    }

    /**
     * Apply movement to the target entity in the given direction from the ability
     * source, with options to flag it as knockback and to reset the entity's fall
     * distance
     * 
     * @param target            The entity to move
     * @param direction         How to move the entity
     * @param source            The ability applying movement
     * @param knockback         Whether the movement was knockback
     * @param resetFallDistance Whether the movement should reset fall distance to
     *                          zero
     */
    public static void move(LivingEntity target, Vector direction, AbilityInstance source, boolean knockback, boolean resetFallDistance) {
        InstanceMoveEntityEvent event = Events.call(new InstanceMoveEntityEvent(target, direction, source, knockback, resetFallDistance));
        if (event.isCancelled()) {
            return;
        }

        if (event.doesResetFallDistance()) {
            target.setFallDistance(0);
        }

        if (knockback) {
            // track knockback for impact damage
        }

        target.setVelocity(direction);
    }

    public static void ignite(LivingEntity target, int fireTicks, AbilityInstance source) {
        InstanceIgniteEntityEvent event = Events.call(new InstanceIgniteEntityEvent(target, fireTicks, source));

        if (event.isCancelled()) {
            return;
        }

        target.setFireTicks(target.getFireTicks() + event.getFireTicks());
    }

    public static void forNearby(Location loc, double range, Predicate<Entity> filter, Consumer<Entity> effect) {
        loc.getWorld().getNearbyEntities(loc, range, range, range, filter).forEach(effect);
    }

    public static void knockback(Entity entity, Vector velocity, AbilityInstance provider) {
        move(entity, velocity, provider, false, true);
    }

    public static void move(Entity entity, Vector velocity, AbilityInstance provider) {
        move(entity, velocity, provider, false, false);
    }

    public static void move(Entity entity, Vector velocity, AbilityInstance provider, boolean lock) {
        move(entity, velocity, provider, lock, false);
    }

    public static void move(Entity entity, Vector velocity, AbilityInstance provider, boolean lock, boolean cumulative) {
        if (entity == null || velocity == null) {
            return;
        } else if (entity.hasMetadata(LOCKED) && !entity.getMetadata(LOCKED).get(0).value().equals(provider)) {
            return;
        } else if (lock && !entity.hasMetadata(LOCKED)) {
            entity.setMetadata(LOCKED, new FixedMetadataValue(Elemental.plugin(), provider));
            Elemental.plugin().getServer().getScheduler().scheduleSyncDelayedTask(Elemental.plugin(), () -> entity.removeMetadata(LOCKED, Elemental.plugin()), 60);
        }

        velocity = velocity.clone();
        if (cumulative) {
            velocity.add(entity.getVelocity());
        }

        entity.setMetadata(METAKEY, new FixedMetadataValue(Elemental.plugin(), provider));
        Elemental.plugin().getServer().getScheduler().scheduleSyncDelayedTask(Elemental.plugin(), () -> entity.removeMetadata(METAKEY, Elemental.plugin()), 60);

        entity.setVelocity(velocity);
    }

    public static boolean isBeingMoved(Entity entity) {
        return entity.hasMetadata(METAKEY);
    }
    
    public static void transformDisplay(Display display, Consumer<Transformation> transformer) {
        Transformation transform = display.getTransformation();
        transformer.accept(transform);
        display.setTransformation(transform);
    }
    
    public static Location getDisplayLocation(Display display) {
        Vector3f translate = display.getTransformation().getTranslation();
        return new Location(display.getWorld(), translate.x, translate.y, translate.z);
    }
}
