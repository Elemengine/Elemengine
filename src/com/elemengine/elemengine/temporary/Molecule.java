package com.elemengine.elemengine.temporary;    

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.elemengine.elemengine.util.spigot.Displays;

import io.papermc.paper.entity.TeleportFlag;

public final class Molecule {
    
    private static final Set<Molecule> ACTIVE = new HashSet<>();
    private static final Brightness LIGHT = new BlockDisplay.Brightness(15, 15);

    private final Set<Fragment> models = new HashSet<>();
    private final Location loc;
    private boolean removeWhenEmpty = true;
    private Entity attached;
    
    public Molecule(Location loc) {
        this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    public Molecule(World world, double x, double y, double z) {
        this.loc = new Location(world, x, y, z);
        ACTIVE.add(this);
    }
    
    public Location getLocation() {
        return loc.clone();
    }
    
    public Vector3f getOffset(Location to) {
        return new Vector3f(
                (float) (to.getX() - loc.getX()), 
                (float) (to.getY() - loc.getY()), 
                (float) (to.getZ() - loc.getZ())
        );
    }
    
    public void setRemoveWhenEmpty(boolean removeWhenEmpty) {
        this.removeWhenEmpty = removeWhenEmpty;
    }
    
    public void attach(Entity vehicle) {
        Location loc = vehicle.getLocation().add(0, vehicle.getHeight(), 0);
        float dx = (float) (this.loc.getX() - loc.getX());
        float dy = (float) (this.loc.getY() - loc.getY());
        float dz = (float) (this.loc.getZ() - loc.getZ());
        
        this.attached = vehicle;
        for (Fragment frag : models) {
            vehicle.addPassenger(frag.entity);
            Displays.transform(frag.entity, transform -> transform.getTranslation().add(dx, dy, dz));
        }
    }
    
    public <T extends Display> void add(Class<T> type, Consumer<T> init, Matrix4f matrix, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        ACTIVE.add(this);
        if (offset == null) {
            offset = new Vector3f();
        }
        Vector3f offsets = offset;
        
        Display entity = loc.getWorld().spawn(loc.add(offsets.x, offsets.y, offsets.z), type, e -> {
            if (this.attached != null) {
                this.attached.addPassenger(e);
            }
            
            init.accept(e);
            e.setTransformationMatrix(matrix.scale(scale));
            e.setTeleportDuration(1);
            e.setBrightness(LIGHT);
        });
        
        loc.subtract(offsets.x, offsets.y, offsets.z);
        models.add(fragmenter.apply(entity));
    }
    
    public <T extends Display> void add(Class<T> type, Consumer<T> init, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        this.add(type, init, new Matrix4f(), scale, offset, fragmenter);
    }
    
    public void add(ItemStack item, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        this.add(ItemDisplay.class, e -> {
            e.setItemStack(item);
        }, new Matrix4f(), scale, offset, fragmenter);
    }
    
    public void add(ItemStack item, float scale, Vector3f offset, Vector3f drift) {
        this.add(item, scale, offset, d -> new Fragment(d, drift));
    }
    
    public void add(BlockData data, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        float centering = -scale/2.0f;
        this.add(BlockDisplay.class, e -> {
            e.setBlock(data);
        }, new Matrix4f().translate(centering, centering, centering), scale, offset, fragmenter);
    }
    
    public void add(BlockData data, float scale, Vector3f offset, Vector3f drift) {
        this.add(data, scale, offset, d -> new Fragment(d, drift));
    }
    
    public void add(Material type, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        this.add(type.createBlockData(), scale, offset, fragmenter);
    }
    
    public void add(Material type, float scale, Vector3f offset, Vector3f drift) {
        this.add(type, scale, offset, d -> new Fragment(d, drift));
    }
    
    public void move(Vector dv) {
        this.move((float) dv.getX(), (float) dv.getY(), (float) dv.getZ());
    }
    
    public void move(double dx, double dy, double dz) {
        this.move((float) dx, (float) dy, (float) dz);
    }
    
    public void move(float dx, float dy, float dz) {
        if (this.attached != null) {
            for (Fragment frag : models) {
                Displays.transform(frag.entity, 0, 1, t -> t.getTranslation().add(dx, dy, dz));
            }
        } else {
            loc.add(dx, dy, dz);
            for (Fragment frag : models) {
                frag.entity.teleport(frag.entity.getLocation().add(dx, dy, dz), TeleportCause.PLUGIN);
            }
        }
    }
    
    public void destroy() {
        for (Fragment frag : models) {
            frag.entity.remove();
        }
        models.clear();
        ACTIVE.remove(this);
    }
    
    private void update() {
        Iterator<Fragment> iter = models.iterator();
        while (iter.hasNext()) {
            if (!iter.next().update()) {
                iter.remove();
            }
        }
    }
    
    static void updateAll() {
        Iterator<Molecule> iter = ACTIVE.iterator();
        while (iter.hasNext()) {
            Molecule molecule = iter.next();
            molecule.update();
            
            if (molecule.removeWhenEmpty && molecule.models.isEmpty()) {
                iter.remove();
            }
        }
    }
    
    public static class Fragment {
        
        private final Display entity;
        private final Vector3f drift;
        
        public Fragment(Display entity, Vector3f drift) {
            this.entity = entity;
            this.drift = drift == null ? new Vector3f() : drift;
        }
        
        protected Transformation generateTransform() {
            return entity.getTransformation();
        }
        
        private final boolean update() {
            Transformation transform = this.generateTransform();
            if (transform == null) {
                entity.remove();
                return false;
            }
            
            entity.teleport(entity.getLocation().add(drift.x, drift.y, drift.z), TeleportCause.PLUGIN);
            entity.setTransformation(transform);
            entity.setInterpolationDelay(0);
            entity.setInterpolationDuration(1);
            entity.setTeleportDuration(1);
            return true;
        }
        
        public final Class<? extends Display> getDisplayType() {
            return entity.getClass();
        }
        
        public final boolean isDisplayType(Class<? extends Display> clazz) {
            return clazz.isAssignableFrom(entity.getClass());
        }
        
        public final <T extends Display> void updateDisplay(Class<T> type, Consumer<T> update) {
            if (!this.isDisplayType(type)) {
                return;
            }
            
            update.accept(type.cast(entity));
        }
    }
    
    public static class ShrinkingFragment extends Fragment {

        private final float shrinkPercent, threshold;
        
        public ShrinkingFragment(Display entity, Vector3f drift, float shrinkPercent) {
            this(entity, drift, shrinkPercent, 0.00001f);
        }
        
        public ShrinkingFragment(Display entity, Vector3f drift, float shrinkPercent, float threshold) {
            super(entity, drift);
            this.shrinkPercent = shrinkPercent;
            this.threshold = threshold;
        }
        
        @Override
        protected Transformation generateTransform() {
            Transformation transform = super.generateTransform();
            Vector3f scale = transform.getScale();
            float diff = (1f - shrinkPercent) / 2f;
            
            if (this.isDisplayType(BlockDisplay.class)) {
                transform.getTranslation().add(scale.x * diff, scale.y * diff, scale.z * diff);
            }
            
            scale.mul(shrinkPercent);
            if (scale.lengthSquared() < threshold) {
                return null;
            }
            
            return transform;
        }
    }
}
