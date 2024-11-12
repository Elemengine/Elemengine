package com.elemengine.elemengine.element.util;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.temporary.Molecule;
import com.elemengine.elemengine.util.math.Vectors;
import com.elemengine.elemengine.util.spigot.Items;

public final class Firebending {
    
    public static final BlockData RED_FIRE = Material.FIRE.createBlockData();
    public static final BlockData BLUE_FIRE = Material.SOUL_FIRE.createBlockData();
    public static final Color[] RAINBOW_COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE };
    public static final ItemStack[] RAINBOW_ITEMS = new ItemStack[RAINBOW_COLORS.length];
    
    static {
        for (int i = 0; i < RAINBOW_ITEMS.length; ++i) {
            int colorIndex = i;
            RAINBOW_ITEMS[i] = Items.create(Material.LEATHER_HORSE_ARMOR, LeatherArmorMeta.class, meta -> {
                meta.setColor(RAINBOW_COLORS[colorIndex]);
                meta.setItemModel(Material.BLAZE_POWDER.getKey());
            });
        }
    }
    
    private Firebending() {}
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset) {
        return spawnFlames(user, amount, x, y, z, offset, null, 0.2f);
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift) {
        return spawnFlames(user, amount, x, y, z, offset, drift, 0.2f);
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift, float scaling) {
        Molecule molecule = new Molecule(user.getWorld(), x, y, z);
        
        for (int i = 0; i < amount; ++i) {
            float scale = ThreadLocalRandom.current().nextFloat() * offset;
            Vector3f offsets = Vectors.random3F().mul(scale);
            Vector3f v = drift == null ? Vectors.random3F().mul(0.0126f) : drift;
            addFlame(molecule, user, scaling, offsets, v);
        }
        
        molecule.setTransformer(Firebending::transformFlame);
        
        return molecule;
    }
    
    public static boolean transformFlame(Transformation transform, Class<? extends Display> type) {
        Vector3f scale = transform.getScale();
        
        if (BlockDisplay.class.isAssignableFrom(type)) {
            transform.getTranslation().add(scale.x * 0.1f, scale.y * 0.1f, scale.z * 0.1f);
        }
        
        scale.mul(0.8f);
        return scale.lengthSquared() < 0.000001;
    }
    
    private static void addFlame(Molecule molecule, AbilityUser user, float scaling, Vector3f offset, Vector3f drift) {
        if (user.hasPermission("elemengine.fire.rainbow_flames")) {
            molecule.add(getRainbow(), scaling, offset, drift);
            return;
        }
        
        molecule.add(getFlameColor(user), scaling, offset, drift);
    }
    
    public static ItemStack getRainbow() {
        return RAINBOW_ITEMS[ThreadLocalRandom.current().nextInt(RAINBOW_COLORS.length)];
    }
    
    public static BlockData getFlameColor(AbilityUser user) {
        return user.hasPermission("elemengine.fire.blue_flames") ? BLUE_FIRE : RED_FIRE;
    }
    
    public static Particle getFlameParticle(AbilityUser user) {
        return user.hasPermission("elemengine.fire.blue_flames") ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
    }
}
