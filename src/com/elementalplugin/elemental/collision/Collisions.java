package com.elementalplugin.elemental.collision;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.util.BoundingBox;

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.storage.CollisionData;
import com.elementalplugin.elemental.storage.CollisionFile;
import com.elementalplugin.elemental.util.data.Pair;

public final class Collisions extends Manager {

    public static final int BOUNDING_MIN = -29999984;
    public static final int BOUNDING_MAX = 29999984;

    private CollisionTree tree = new CollisionTree(new BoundingBox(BOUNDING_MIN, BOUNDING_MIN, BOUNDING_MIN, BOUNDING_MAX, BOUNDING_MAX, BOUNDING_MAX), 10);
    private Set<Collider> seen = new HashSet<>(), removal = new HashSet<>();
    private Map<Pair<String>, CollisionData> valids = new HashMap<>();

    @Override
    protected int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean active() {
        return true;
    }

    @Override
    protected void startup() {
        CollisionFile.open(new File(Elemental.getFolder(), "collisions.txt")).readAnd((valid) -> {
            valids.put(Pair.of(valid.getLeft(), valid.getRight()), valid);
        });
    }

    @Override
    protected void tick() {
        Collider.ACTIVE.forEach(tree::insert);

        Collider.ACTIVE.forEach(collider -> {
            seen.add(collider);

            tree.queryAnd(collider, other -> treeFilter(collider, other), (tqr) -> {
                Collider other = tqr.getCollided();
                CollisionData data = this.getDataFor(collider.tag, other.tag);

                if (data.isRemoved(collider.tag)) {
                    removal.add(collider);
                }

                if (data.isRemoved(other.tag)) {
                    removal.add(other);
                }

                collider.collided(tqr);
                other.collided(collider, tqr.getCollidingBounds());
            });
        });

        removal.forEach(Collider::removal);
        this.clear();
    }

    @Override
    protected void clean() {
        this.clear();
        valids.clear();
    }

    private void clear() {
        tree.clear();
        seen.clear();
        removal.clear();
    }

    private boolean treeFilter(Collider outer, Collider inner) {
        return outer.getWorld().equals(inner.getWorld()) && isValid(outer, inner) && !seen.contains(inner);
    }

    public boolean isValid(Collider one, Collider two) {
        if (one == null || two == null) {
            return false;
        }

        return valids.containsKey(Pair.of(one.tag, two.tag));
    }

    public CollisionData getDataFor(String tagA, String tagB) {
        return valids.get(Pair.of(tagA, tagB));
    }

    public boolean addValid(CollisionData data) {
        if (valids.containsKey(data.getTags())) {
            return false;
        }

        valids.put(data.getTags(), data);
        return true;
    }
}
