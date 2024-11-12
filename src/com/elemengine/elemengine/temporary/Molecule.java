package com.elemengine.elemengine.temporary;    

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
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

public final class Molecule {
    
    private static final Set<Molecule> ACTIVE = new HashSet<>();
    private static final Brightness LIGHT = new BlockDisplay.Brightness(15, 15);

    private final Set<Fragment> models = new HashSet<>();
    private final Location loc;
    private Predicate<Fragment> transformer;
    private boolean removeWhenEmpty = true;
    private Entity attached;
    
    public Molecule(Location loc) {
        this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    public Molecule(World world, double x, double y, double z) {
        this.loc = new Location(world, x, y, z);
        this.transformer = null;
        ACTIVE.add(this);
    }
    
    public Location getLocation() {
        return loc.clone();
    }
    
    public void setTransformer(Predicate<Fragment> transformer) {
        this.transformer = transformer;
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
        
        Display entity = loc.getWorld().spawn(loc, type, e -> {
            if (this.attached != null) {
                this.attached.addPassenger(e);
            }
            
            init.accept(e);
            e.setTransformationMatrix(matrix.translate(offset).scale(scale));
            e.setTeleportDuration(1);
            e.setBrightness(LIGHT);
        });
        
        models.add(fragmenter.apply(entity));
    }
    
    public <T extends Display> void add(Class<T> type, Consumer<T> init, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        this.add(type, init, new Matrix4f(), scale, offset, fragmenter);
    }
    
    public void add(ItemStack item, float scale, Vector3f offset, Function<Display, Fragment> fragmenter) {
        this.add(ItemDisplay.class, e -> {
            e.setBillboard(Billboard.CENTER);
            e.setItemStack(item);
        }, new Matrix4f(), scale, offset, fragmenter);
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
                frag.entity.teleport(loc, TeleportCause.PLUGIN);
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
        if (transformer == null) {
            return;
        }
        
        Iterator<Fragment> iter = models.iterator();
        while (iter.hasNext()) {
            if (iter.next().update(transformer)) {
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
        
        protected Fragment(Display entity, Vector3f drift) {
            this.entity = entity;
            this.drift = drift;
        }
        
        private boolean update(Predicate<Fragment> transformer) {
            if (transformer.test(this)) {
                entity.remove();
                return false;
            }
            
            Displays.transform(entity, 0, 1, t -> t.getTranslation().add(drift));
            entity.setTeleportDuration(1);
            return true;
        }
        
        public Class<? extends Display> getDisplayType() {
            return entity.getClass();
        }
        
        public Transformation getTransformation() {
            return entity.getTransformation();
        }
    }
}
