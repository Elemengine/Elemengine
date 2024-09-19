package com.elemengine.elemengine.collision;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.collision.CollisionTree.TreeQueryResult;
import com.elemengine.elemengine.util.data.Pair;

public class Collider {

    static Set<Collider> ACTIVE = new HashSet<>();

    public final String tag;

    private Set<BoundingBox> boxes = new HashSet<>();
    private Location center;
    private Consumer<TreeQueryResult> collided;
    private Runnable removal;

    public Collider(String tag, Location center, Consumer<TreeQueryResult> collided, Runnable removal) {
        this.tag = tag;
        this.center = center;
        this.collided = collided;
        this.removal = removal;
    }
    
    public Collider(String tag, Location center, AbilityInstance<?> removal, Consumer<TreeQueryResult> collided) {
        this.tag = tag;
        this.center = center;
        this.collided = collided;
        this.removal = () -> Abilities.manager().stopInstance(removal);
    }
    
    public Collider(String tag, Location center, AbilityInstance<?> removal) {
        this(tag, center, removal, null);
    }

    Location center() {
        return center;
    }

    void collided(TreeQueryResult result) {
        if (result == null || collided == null) {
            return;
        }

        collided.accept(result);
    }

    void collided(Collider other, Pair<BoundingBox> intsect) {
        if (other == null || intsect == null || collided == null) {
            return;
        }

        collided.accept(new TreeQueryResult(other, intsect));
    }

    void removal() {
        if (removal == null)
            return;

        removal.run();
    }

    public World getWorld() {
        return center.getWorld();
    }

    public Collider enable() {
        ACTIVE.add(this);
        return this;
    }

    public Collider disable() {
        ACTIVE.remove(this);
        return this;
    }

    public Collider clear() {
        this.boxes.clear();
        return this;
    }

    public boolean isActive() {
        return ACTIVE.contains(this);
    }
    
    public void add(double sideLength) {
        this.boxes.add(BoundingBox.of(center, sideLength, sideLength, sideLength));
    }
    
    public void add(double length, double height, double width) {
        this.boxes.add(BoundingBox.of(center, length, height, width));
    }
    
    public void add(Location loc, double sideLength) {
        this.boxes.add(BoundingBox.of(loc, sideLength, sideLength, sideLength));
    }
    
    public void add(Location loc, double length, double height, double width) {
        this.boxes.add(BoundingBox.of(loc, length, height, width));
    }

    public void add(BoundingBox box) {
        this.boxes.add(box);
    }

    public void reset(Collection<BoundingBox> boxes) {
        this.boxes.clear();
        this.boxes.addAll(boxes);
    }

    public void set(Collection<BoundingBox> boxes) {
        this.boxes.addAll(boxes);
    }

    public Set<Pair<BoundingBox>> intersections(Collider other) {
        if (this == other) {
            return new HashSet<>();
        }

        Set<Pair<BoundingBox>> inters = new HashSet<>();
        for (BoundingBox box : boxes) {
            for (BoundingBox otherBox : other.boxes) {
                if (box.overlaps(otherBox)) {
                    inters.add(Pair.of(box, otherBox));
                }
            }
        }

        return inters;
    }

    public boolean overlaps(BoundingBox aabb) {
        for (BoundingBox box : boxes) {
            if (box.overlaps(aabb)) {
                return true;
            }
        }

        return false;
    }

    public double getVolume() {
        double volume = 0;
        for (BoundingBox box : boxes) {
            volume += box.getVolume();
        }

        return volume;
    }
}
