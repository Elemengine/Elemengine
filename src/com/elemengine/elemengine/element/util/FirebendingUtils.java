package com.elemengine.elemengine.element.util;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.temporary.Molecule;
import com.elemengine.elemengine.util.math.Vectors;

public interface FirebendingUtils {
    
    BlockData RED_FIRE = Material.FIRE.createBlockData();
    BlockData BLUE_FIRE = Material.SOUL_FIRE.createBlockData();
    
    default Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset) {
        return this.spawnFlames(user, amount, x, y, z, offset, null, 0.2f);
    }
    
    default Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift) {
        return this.spawnFlames(user, amount, x, y, z, offset, drift, 0.2f);
    }
    
    default Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift, float scaling) {
        Molecule molecule = new Molecule(user.getWorld(), x, y, z);
        BlockData color = getFlameColor(user);
        
        for (int i = 0; i < amount; ++i) {
            float scale = ThreadLocalRandom.current().nextFloat() * offset;
            Vector3f offsets = Vectors.random3F().mul(scale);
            Vector3f v = drift == null ? Vectors.random3F().mul(0.0126f) : drift;
            molecule.add(color, scaling, offsets, v);
        }
        
        molecule.setTransformer(this::transformFlame);
        
        return molecule;
    }
    
    default boolean transformFlame(Transformation transform) {
        Vector3f scale = transform.getScale();
        float dx = scale.x * 0.1f;
        float dy = scale.y * 0.1f;
        float dz = scale.z * 0.1f;
        
        scale.mul(0.8f);
        transform.getTranslation().add(dx, dy, dz);
        return scale.lengthSquared() < 0.000001;
    }
    
    default BlockData getFlameColor(AbilityUser user) {
        return user.hasPermission("elemengine.fire.blue_flames") ? BLUE_FIRE : RED_FIRE;
    }
}
