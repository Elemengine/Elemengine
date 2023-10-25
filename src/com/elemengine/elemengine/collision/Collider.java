package com.elemengine.elemengine.collision;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

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

    Location center() {
        return center;
    }

    public void collided(TreeQueryResult result) {
        if (result == null || collided == null) {
            return;
        }

        collided.accept(result);
    }

    public void collided(Collider other, Pair<BoundingBox> intsect) {
        if (other == null || intsect == null || collided == null) {
            return;
        }

        collided.accept(new TreeQueryResult(other, intsect));
    }

    public void removal() {
        if (removal == null)
            return;

        removal.run();
    }

    public World getWorld() {
        return center.getWorld();
    }

    public void enable() {
        ACTIVE.add(this);
    }

    public void disable() {
        ACTIVE.remove(this);
    }

    public boolean isActive() {
        return ACTIVE.contains(this);
    }

    public void add(BoundingBox box) {
        this.boxes.add(box);
    }

    public void clear() {
        this.boxes.clear();
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
