package com.elemengine.elemengine.temporary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Molecule {
    
    private static final Set<Molecule> ACTIVE = new HashSet<>();
    private static final Brightness LIGHT = new BlockDisplay.Brightness(15, 15);

    private final Map<BlockDisplay, Vector3f> models = new HashMap<>();
    private final Location loc;
    private Function<Transformation, Boolean> transformer;
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
    
    public void setTransformer(Function<Transformation, Boolean> transformer) {
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
        for (BlockDisplay display : models.keySet()) {
            vehicle.addPassenger(display);
            Transformation transform = display.getTransformation();
            transform.getTranslation().add(dx, dy, dz);
            display.setTransformation(transform);
        }
    }
    
    public void add(Material type, float scale, Vector3f offset, Vector3f drift) {
        this.add(type.createBlockData(), scale, offset, drift);
    }
    
    public void add(BlockData data, float scale, Vector3f offset, Vector3f drift) {
        ACTIVE.add(this);
        float centering = -scale/2.0f;
        
        models.put(loc.getWorld().spawn(loc, BlockDisplay.class, display -> {
            if (this.attached != null) {
                this.attached.addPassenger(display);
            }
            display.setTeleportDuration(1);
            display.setTransformationMatrix(new Matrix4f().translate(centering,centering,centering).translate(offset).scale(scale));
            display.setBlock(data);
            display.setBrightness(LIGHT);
        }), drift);
    }
    
    public void move(Vector dv) {
        this.move(dv.getX(), dv.getY(), dv.getZ());
    }
    
    public void move(double dx, double dy, double dz) {
        if (this.attached != null) {
            for (BlockDisplay model : models.keySet()) {
                Transformation transform = model.getTransformation();
                transform.getTranslation().add((float) dx, (float) dy, (float) dz);
                model.setTransformation(transform);
                model.setInterpolationDelay(0);
                model.setInterpolationDuration(1);
            }
        } else {
            loc.add(dx, dy, dz);
            for (BlockDisplay model : models.keySet()) {
                model.teleport(loc, TeleportCause.PLUGIN);
            }
        }
    }
    
    public void destroy() {
        for (BlockDisplay model : models.keySet()) {
            model.remove();
        }
        models.clear();
        ACTIVE.remove(this);
    }
    
    private void update() {
        if (transformer == null) {
            return;
        }
        
        Iterator<Entry<BlockDisplay, Vector3f>> iter = models.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<BlockDisplay, Vector3f> model = iter.next();
            Transformation transform = model.getKey().getTransformation();
            if (Boolean.TRUE.equals(transformer.apply(transform))) {
                iter.remove();
                model.getKey().remove();
                continue;
            }
            
            transform.getTranslation().add(model.getValue());
            
            model.getKey().setTransformation(transform);
            model.getKey().setInterpolationDelay(0);
            model.getKey().setInterpolationDuration(1);
            model.getKey().setTeleportDuration(1);
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
}
