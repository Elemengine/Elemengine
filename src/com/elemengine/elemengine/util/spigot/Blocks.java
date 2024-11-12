package com.elemengine.elemengine.util.spigot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public final class Blocks {
    
    private static final BlockFace[] CARDINAL = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] SPATIAL = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.DOWN, BlockFace.SOUTH, BlockFace.WEST};

    private Blocks() {}

    /**
     * Finds the blocks along the given ray (dir + length) and passes them to
     * the given Consumer
     * @param start the Location to start at
     * @param dir the direction of the line
     * @param length the length of the line
     * @param forEach what to do with each block
     */
    public static void alongLine(Location start, Vector dir, int length, Consumer<Block> forEach) {
        if (start.getWorld() == null) return;

        BlockIterator iter = new BlockIterator(start.getWorld(), start.toVector(), dir, 0, length);
        while (iter.hasNext()) {
            forEach.accept(iter.next());
        }
    }

    /**
     * Calculates the distance between two blocks
     * @param a any block
     * @param b any block
     * @return distance between the given blocks
     */
    public static double distance(Block a, Block b) {
        return Math.sqrt(distanceSquared(a, b));
    }

    /**
     * Calculates the squared distance between two blocks. It is
     * a faster calculation than the normal {@link Blocks#distance(Block, Block)}
     * method since it does not do a square root.
     * @param a any block
     * @param b any block
     * @return squared distance between the given blocks
     */
    public static double distanceSquared(Block a, Block b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();
        return x * x + y * y + z * z;
    }

    public static double distanceSquared(Block block, Location loc) {
         double x = block.getX() - loc.getX();
         double y = block.getY() - loc.getY();
         double z = block.getZ() - loc.getZ();
         return x * x + y * y + z * z;
    }

    /**
     * Finds each block in a filled circle with the given center location
     * and radius, passing each block to the given Consumer.
     * @param center Center location of the circle
     * @param radius Radius of the circle
     * @param forEach what to do with each block in the circle
     */
    public static void forCircle(Block center, double radius, Consumer<Block> forEach) {
        Set<Block> visited = new HashSet<>();
        visited.add(center);

        Queue<Block> searchQueue = new LinkedList<>();
        searchQueue.add(center);

        double sqRadius = radius * radius;
        
        while (!searchQueue.isEmpty()) {
            Block current = searchQueue.poll();

            for (BlockFace face : CARDINAL) {
                 Block block = current.getRelative(face);
                 if (!visited.contains(block) && Blocks.distanceSquared(center, block) <= sqRadius) {
                     searchQueue.add(block);
                     visited.add(block);
                 }
            }

            forEach.accept(current);
        }
    }
    
    public static void forCircle(Location center, double radius, Consumer<Block> forEach) {
        Blocks.forCircle(center.getBlock(), radius, forEach);
    }

    public static void forNearby(Block center, double radius, Consumer<Block> forEach) {
        Set<Block> visited = new HashSet<>();
        visited.add(center);

        Queue<Block> searchQueue = new LinkedList<>();
        searchQueue.add(center);

        double sqRadius = radius * radius;

        while (!searchQueue.isEmpty()) {
            Block current = searchQueue.poll();

            for (BlockFace face : SPATIAL) {
                Block block = current.getRelative(face);
                if (!visited.contains(block) && Blocks.distanceSquared(center, block) <= sqRadius) {
                    searchQueue.add(block);
                    visited.add(block);
                }
            }

            forEach.accept(current);
        }
    }
    
    public static void forNearby(Location center, double radius, Consumer<Block> forEach) {
        Blocks.forNearby(center.getBlock(), radius, forEach);
    }

    public static Block findTop(Location loc, double range) {
        return findTop(loc.getBlock(), range, Block::isPassable);
    }

    public static Block findTop(Location loc, double range, Predicate<Block> passable) {
        return findTop(loc.getBlock(), range, passable);
    }

    public static Block findTop(Block block, double range) {
        return findTop(block, range, Block::isPassable);
    }

    public static Block findTop(Block block, double range, Predicate<Block> passable) {
        Block curr = block;
        BlockFace v = !passable.test(curr) ? BlockFace.UP : BlockFace.DOWN;
        IntPredicate inRange = !passable.test(curr) ? i -> i < range : i -> i <= range;
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

    public static Block targeted(Location start, double maxDistance, Predicate<Block> ignore) {
        return targeted(start, start.getDirection(), maxDistance, ignore);
    }

    public static Block targeted(Location start, Vector direction, double maxDistance, Predicate<Block> ignore) {
        if (direction.lengthSquared() <= Vector.getEpsilon()) {
            return start.getBlock();
        }

        Location curr = start.clone();
        Vector dir = direction.clone().normalize();
        ignore = ignore.and(Block::isEmpty);

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
