package com.elementalplugin.elemental.collision;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.util.BoundingBox;

import com.elementalplugin.elemental.util.data.Pair;

public class CollisionTree {

    private static final int[] XS = { 1, 1, 1, 1, 0, 0, 0, 0
    };
    private static final int[] YS = { 1, 1, 0, 0, 1, 1, 0, 0
    };
    private static final int[] ZS = { 1, 0, 1, 0, 1, 0, 1, 0
    };

    private BoundingBox bounds;
    private int capacity;
    private Set<Collider> contents;
    private CollisionTree[] children;

    public CollisionTree(BoundingBox bounds, int capacity) {
        this.bounds = bounds;
        this.capacity = capacity;
        this.contents = new HashSet<>(capacity);
        this.children = null;
    }

    private void divide() {
        if (children == null) {
            children = new CollisionTree[8];
            for (int i = 0; i < 8; i++) {
                double[] min = { bounds.getMinX() + XS[i] * bounds.getWidthX() / 2, bounds.getMinY() + YS[i] * bounds.getHeight() / 2, bounds.getMinZ() + ZS[i] * bounds.getWidthZ() / 2
                };
                double[] max = { bounds.getMaxX() - XS[7 - i] * bounds.getWidthX() / 2, bounds.getMaxY() - YS[7 - i] * bounds.getHeight() / 2, bounds.getMaxZ() - ZS[7 - i] * bounds.getWidthZ() / 2
                };
                children[i] = new CollisionTree(new BoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]), capacity);
            }

            for (Collider obj : contents) {
                insert(obj);
            }

            contents.clear();
        }
    }

    void clear() {
        if (children != null) {
            for (CollisionTree branch : children) {
                branch.clear();
            }

            children = null;
        }

        contents.clear();
    }

    public boolean insert(Collider obj) {
        if (!bounds.contains(obj.center().toVector())) {
            return false;
        }

        if (contents.size() + 1 > capacity) {
            this.divide();
        }

        if (children != null) {
            boolean inserted = false;
            int i = 0;

            while (!inserted && i < children.length) {
                inserted = children[i++].insert(obj);
            }

            return inserted;
        }

        return contents.add(obj);
    }

    public Set<TreeQueryResult> query(Collider range, Predicate<Collider> filter) {
        Set<TreeQueryResult> found = new HashSet<>();
        if (!range.overlaps(bounds)) {
            return found;
        }

        if (children != null) {
            for (CollisionTree branch : children) {
                found.addAll(branch.query(range, filter));
            }
        } else {
            for (Collider obj : contents) {
                if (!filter.test(obj)) {
                    continue;
                }

                for (Pair<BoundingBox> intsect : range.intersections(obj)) {
                    found.add(new TreeQueryResult(obj, intsect));
                }
            }
        }

        return found;
    }

    public void queryAnd(Collider range, Predicate<Collider> filter, Consumer<TreeQueryResult> and) {
        if (!range.overlaps(bounds)) {
            return;
        }

        if (children != null) {
            for (CollisionTree branch : children) {
                branch.queryAnd(range, filter, and);
            }
        } else {
            for (Collider obj : contents) {
                if (!filter.test(obj)) {
                    continue;
                }

                for (Pair<BoundingBox> intsect : range.intersections(obj)) {
                    and.accept(new TreeQueryResult(obj, intsect));
                }
            }
        }
    }

    public Set<Collider> getContents() {
        Set<Collider> all = new HashSet<>();

        if (children != null) {
            for (CollisionTree branch : children) {
                all.addAll(branch.getContents());
            }
        } else {
            all.addAll(contents);
        }

        return all;
    }

    public static class TreeQueryResult {
        private Collider collided;
        private Pair<BoundingBox> intersection;

        public TreeQueryResult(Collider collided, Pair<BoundingBox> intersection) {
            this.collided = collided;
            this.intersection = intersection;
        }

        public Collider getCollided() {
            return collided;
        }

        /**
         * Returns the intersection pair for the found collision, left is the
         * boundingbox from the queried collider and right is from the found collidable
         * 
         * @return intersecting boundingboxes
         */
        public Pair<BoundingBox> getCollidingBounds() {
            return intersection;
        }
    }
}
