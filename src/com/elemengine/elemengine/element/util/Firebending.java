package com.elemengine.elemengine.element.util;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.temporary.Molecule;
import com.elemengine.elemengine.temporary.Molecule.Fragment;
import com.elemengine.elemengine.util.math.Gradient;
import com.elemengine.elemengine.util.math.Maths;
import com.elemengine.elemengine.util.math.Vectors;
import com.elemengine.elemengine.util.spigot.Colors;

public final class Firebending {
    
    public static interface FlameColor {
        Gradient get();
    }
    
    public static final FlameColor RED_FIRE = new FlameColor() {
        
        private final Color[] colors = {
            Color.fromRGB(252, 211, 3),
            Color.fromRGB(255, 136, 0),
            Color.BLACK.setAlpha(63),
            Color.BLACK.setAlpha(0)
        };
        
        @Override
        public Gradient get() {
            int[] rgba = {255, 64, 0, 127};
            
            double adj = 30 * (Math.random() - 0.5);
            
            for (int i = 0; i < 3; ++i) {
                rgba[i] = (int) Maths.clamp(rgba[i] + adj, 0, 255);
            }
            
            return Gradient.builder()
                    .add(colors[0]).add(colors[1])
                    .add(Colors.fromRGBA(rgba), 6)
                    .add(colors[2]).end(colors[3]);
        }
    };
    
    public static final FlameColor BLUE_FIRE = new FlameColor() {

        private final Color[] colors = {
            Color.fromRGB(140, 247, 252),
            Color.fromRGB(31, 205, 251),
            Color.BLACK.setAlpha(63),
            Color.BLACK.setAlpha(0)
        };
        
        @Override
        public Gradient get() {
            int[] rgba = {14, 115, 240, 127};
            
            double adj = 30 * (Math.random() - 0.5);
            
            for (int i = 0; i < 3; ++i) {
                rgba[i] = (int) Maths.clamp(rgba[i] + adj, 0, 255);
            }
            
            return Gradient.builder()
                    .add(colors[0]).add(colors[1])
                    .add(Colors.fromRGBA(rgba), 6)
                    .add(colors[2]).end(colors[3]);
        }
    };
    
    public static final FlameColor RAINBOW_FIRE = new FlameColor() {
        
        private final Gradient rainbow = Gradient.builder()
                .add(Color.RED).add(Color.fromRGB(0xff8000))
                .add(Color.YELLOW).add(Color.fromRGB(0x80ff00))
                .add(Color.LIME).add(Color.fromRGB(0x00ff80))
                .add(Color.AQUA).add(Color.fromRGB(0x0080ff))
                .add(Color.BLUE).add(Color.fromRGB(0x8000ff))
                .add(Color.FUCHSIA).add(Color.fromRGB(0xff0080))
                .end(Color.RED);
        
        private int calls = 0;
        
        @Override
        public Gradient get() {
            calls = (calls + 1) % 3600;
            double value = ((double) calls) / 3600d;
            
            return Gradient.builder()
                    .add(rainbow.getColor(value), 7)
                    .add(Color.fromRGB(255, 136, 0))
                    .add(Color.BLACK.setAlpha(63))
                    .end(Color.BLACK.setAlpha(0));
        }
    };
    
    private Firebending() {}
    
    // FIRE --------------------------------------------------------------
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift, float scaling) {
        Molecule molecule = new Molecule(user.getWorld(), x, y, z);
        
        for (int i = 0; i < amount; ++i) {
            float scale = ThreadLocalRandom.current().nextFloat() * offset;
            Vector3f offsets = Vectors.random3F().mul(scale);
            
            Vector3f v = drift;
            if (v == null) {
                v = Vectors.random3F().mul(0.0526f);
                v.y = Math.abs(v.y);
            }
            
            addFlame(molecule, user, scaling, offsets, v);
        }
        
        return molecule;
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift) {
        return spawnFlames(user, amount, x, y, z, offset, drift, 1.0f);
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, double x, double y, double z, float offset) {
        return spawnFlames(user, amount, x, y, z, offset, null, 1.0f);
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, Location loc, float offset) {
        return spawnFlames(user, amount, loc.getX(), loc.getY(), loc.getZ(), offset, null, 1.0f);
    }
    
    public static Molecule spawnFlames(AbilityUser user, int amount, Location loc) {
        return spawnFlames(user, amount, loc.getX(), loc.getY(), loc.getZ(), 0, null, 1.0f);
    }
    
    public static void addFlame(Molecule molecule, AbilityUser user, float scaling, Vector3f offset, Vector3f drift) {
        FlameColor color = RED_FIRE;
        if (user.hasPermission("elemengine.fire.rainbow_flames")) {
            color = RAINBOW_FIRE;
        } else if (user.hasPermission("elemengine.fire.blue_flames")) {
            color = BLUE_FIRE;
        }
        
        Gradient picked = color.get();
        
        molecule.add(TextDisplay.class, td -> {
            td.setText(" ");
            td.setBackgroundColor(picked.getColor(0));
            td.setBillboard(Billboard.CENTER);
        }, new Matrix4f().scale(2f, 1f, 1f), scaling, offset, d -> new FlameFragment(d, drift, picked));
    }
    
    public static void addFlame(Molecule molecule, AbilityUser user, float scaling, Vector3f offset) {
        Vector3f drift = Vectors.random3F().mul(0.0526f);
        drift.y = Math.abs(drift.y);
        addFlame(molecule, user, scaling, offset, drift);
    }
    
    // LIGHTNING --------------------------------------------------------------
    
    public static Molecule spawnSparks(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift, float scaling) {
        Molecule molecule = new Molecule(user.getWorld(), x, y, z);
        
        for (int i = 0; i < amount; ++i) {
            float scale = ThreadLocalRandom.current().nextFloat() * offset;
            Vector3f offsets = Vectors.random3F().mul(scale);
            Vector3f v = drift == null ? Vectors.random3F().mul(0.0126f) : drift;
            addSpark(molecule, user, scaling, offsets, v);
        }
        
        return molecule;
    }
    
    public static Molecule spawnSparks(AbilityUser user, int amount, double x, double y, double z, float offset, Vector3f drift) {
        return spawnSparks(user, amount, x, y, z, offset, drift, 1.0f);
    }
    
    public static Molecule spawnSparks(AbilityUser user, int amount, double x, double y, double z, float offset) {
        return spawnSparks(user, amount, x, y, z, offset, null, 1.0f);
    }
    
    public static Molecule spawnSparks(AbilityUser user, int amount, Location loc, float offset) {
        return spawnSparks(user, amount, loc.getX(), loc.getY(), loc.getZ(), offset, null, 1.0f);
    }
    
    public static Molecule spawnSparks(AbilityUser user, int amount, Location loc) {
        return spawnSparks(user, amount, loc.getX(), loc.getY(), loc.getZ(), 0, null, 1.0f);
    }
    
    public static void addSpark(Molecule molecule, AbilityUser user, float scaling, Vector3f offset, Vector3f drift) {
        molecule.add(TextDisplay.class, td -> {
            td.setText(" ");
            td.setBackgroundColor(Color.TEAL);
            td.setBillboard(Billboard.CENTER);
        }, new Matrix4f().scale(2f, 1f, 1f), scaling, offset, d -> new SparkFragment(d, drift));
    }
    
    public static void addSpark(Molecule molecule, AbilityUser user, float scaling, Vector3f offset) {
        addSpark(molecule, user, scaling, offset, Vectors.random3F().mul(0.0126f));
    }
    
    // UTILS --------------------------------------------------------------
    
    private static class FlameFragment extends Fragment {
        
        private int age = 0;
        
        private final Gradient gradient;
        private final float[] scale = {1f, 1f, 1f};
        private final float rotSpeed = ThreadLocalRandom.current().nextFloat(-0.2f, 0.2f);
        private final int lifeTime = ThreadLocalRandom.current().nextInt(10, 18);

        public FlameFragment(Display entity, Vector3f drift, Gradient gradient) {
            super(entity, drift);
            this.gradient = gradient;
            Vector3f scale = entity.getTransformation().getScale();
            this.scale[0] = scale.x;
            this.scale[1] = scale.y;
            this.scale[2] = scale.z;
        }
        
        @Override
        protected Transformation generateTransform() {
            Transformation transform = super.generateTransform();
            
            double agePercent = ((double) age++) / lifeTime;
            if (agePercent >= 1.0) {
                return null;
            }
            
            transform.getLeftRotation().rotateZ(rotSpeed);
            transform.getScale().set(scale).mul((float) (1.0 - agePercent) * 0.8f + 0.2f);
            
            this.updateDisplay(TextDisplay.class, td -> {
                td.setBackgroundColor(gradient.getColor(agePercent));
            });
            
            return transform;
        }
    }
    
    private static class SparkFragment extends Fragment {

        private static final Gradient GRADIENT = Gradient.builder().add(Color.AQUA).end(Color.WHITE);
        
        private int age = 0;
        
        private final float[] scale = {1f, 1f, 1f};
        private final int lifeTime = ThreadLocalRandom.current().nextInt(5, 10);
        private final float rot = ThreadLocalRandom.current().nextFloat((float) Maths.TAU);
        
        public SparkFragment(Display entity, Vector3f drift) {
            super(entity, drift);
            Vector3f scale = entity.getTransformation().getScale();
            this.scale[0] = scale.x;
            this.scale[1] = scale.y;
            this.scale[2] = scale.z;
        }
        
        @Override
        protected Transformation generateTransform() {
            Transformation transform = super.generateTransform();
            
            double agePercent = ((double) age++) / lifeTime;
            if (agePercent >= 1.0) {
                return null;
            }
            
            transform.getLeftRotation().rotationZ(rot);
            transform.getScale().set(scale).mul((float) (1.0 - agePercent) * 0.8f + 0.2f);
            
            this.updateDisplay(TextDisplay.class, td -> {
                td.setBackgroundColor(GRADIENT.getColor(agePercent));
            });
            
            return transform;
        }
    }
}
