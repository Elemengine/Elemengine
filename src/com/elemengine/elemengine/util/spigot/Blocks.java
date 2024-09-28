package com.elemengine.elemengine.util.spigot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public final class Blocks {
    
    private static final BlockFace[] HORIZONTAL_CIRCLE_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final int[][] CORNERS = {{0, 0}, {1, 1}, {-1, 0}, {1, -1}};

    private Blocks() {}
    
    public static void alongLine(Location start, Vector dir, int length, Consumer<Block> forEach) {
        BlockIterator iter = new BlockIterator(start.getWorld(), start.toVector(), dir, 0, length);
        while (iter.hasNext()) {
            forEach.accept(iter.next());
        }
    }

    public static double distance(Block a, Block b) {
        return Math.sqrt(distanceSquared(a, b));
    }

    public static double distanceSquared(Block a, Block b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();
        return x * x + y * y + z * z;
    }
    
    public static void forBlockCircle(Location center, double radius, Consumer<Block> forEach) {
        Queue<Block> searchQueue = new LinkedList<>();
        searchQueue.add(center.getBlock());
        boolean empty = false;
        Location cornerCheck = center.clone();
        
        while (!empty) {
            Block current = searchQueue.poll();

            for (BlockFace face : HORIZONTAL_CIRCLE_FACES) {
                 Block block = current.getRelative(face);
                 if (searchQueue.contains(block)) {
                     continue;
                 }
                 
                 block.getLocation(cornerCheck);
                 
                 for (int[] xz : CORNERS) {
                     cornerCheck.add(xz[0], 0, xz[1]);
                     if (cornerCheck.distanceSquared(center) <= radius * radius) {
                         searchQueue.add(block);
                         break;
                     }
                 }
            }

            forEach.accept(current);
            empty = searchQueue.isEmpty();
        }
    }

    public static void forNearby(Location center, double radius, Consumer<Block> forEach) {
        forNearby(center.getBlock(), radius, forEach);
    }

    public static void forNearby(Block center, double radius, Consumer<Block> forEach) {
        for (int x = (int) -(radius + 1); x <= radius; ++x) {
            for (int y = (int) -(radius + 1); y <= radius; ++y) {
                for (int z = (int) -(radius + 1); z <= radius; ++z) {
                    Block block = center.getRelative(x, y, z);
                    if (distanceSquared(block, center) > radius * radius) {
                        continue;
                    }

                    forEach.accept(block);
                }
            }
        }
    }

    public static Block findTop(Location loc, double range) {
        return findTop(loc.getBlock(), range, (b) -> b.isPassable());
    }

    public static Block findTop(Location loc, double range, Predicate<Block> passable) {
        return findTop(loc.getBlock(), range, passable);
    }

    public static Block findTop(Block block, double range) {
        return findTop(block, range, (b) -> b.isPassable());
    }

    public static Block findTop(Block block, double range, Predicate<Block> passable) {
        Block curr = block;
        BlockFace v = !passable.test(curr) ? BlockFace.UP : BlockFace.DOWN;
        Predicate<Integer> inRange = !passable.test(curr) ? (i) -> i < range : (i) -> i <= range;
        int i = 0;

        while (inRange.test(i)) {
            if (v == BlockFace.UP && passable.test(curr.getRelative(BlockFace.UP))) {
                break;
            }

            curr = curr.getRelative(v);
            ++i;

            if (v == BlockFace.DOWN && !passable.test(curr)) {
                break;
            }
        }

        return curr;
    }

    public static Set<Block> nearby(Location center, double radius) {
        return nearby(center.getBlock(), radius);
    }

    public static Set<Block> nearby(Block center, double radius) {
        Set<Block> blocks = new HashSet<>();

        for (int x = (int) -(radius + 1); x <= radius; ++x) {
            for (int y = (int) -(radius + 1); y <= radius; ++y) {
                for (int z = (int) -(radius + 1); z <= radius; ++z) {
                    Block block = center.getRelative(x, y, z);
                    if (distanceSquared(block, center) > radius * radius) {
                        continue;
                    }
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static Block targeted(Location start, double maxDistance, Predicate<Block> ignore) {
        return targeted(start, start.getDirection(), maxDistance, ignore);
    }

    public static Block targeted(Location start, Vector direction, double maxDistance, Predicate<Block> ignore) {
        if (direction.lengthSquared() <= Vector.getEpsilon()) {
            return start.getBlock();
        }

        Location curr = start.clone();
        Vector dir = direction.clone().normalize();
        ignore = ignore.and((b) -> b.isEmpty());

        for (double d = 0; d < maxDistance; d += 1) {
            Block block = curr.add(dir).getBlock();

            if (ignore.test(block)) {
                continue;
            }

            return block;
        }

        return curr.getBlock();
    }
}
