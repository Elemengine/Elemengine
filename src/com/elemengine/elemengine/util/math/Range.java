package com.elemengine.elemengine.util.math;

import org.bukkit.Location;

public interface Range<T> {

    /**
     * Runs a check to test if the range is acceptable.
     * <br><br>
     * A proper implementation will return true if the given value
     * is still within range.
     * <br><br>
     * Two implementations are provided for convenience, 
     * see {@link Range.Traveled} and {@link Range.Distance}.
     * @param obj whatever object the implementation is expecting
     * @return true if the given value does not push the implementation to exceed its range.
     */
    boolean check(T obj);
    
    /**
     * Implementation of Range that keeps track of a displacement.
     * <br><br>
     * The value given to {@link #check(Double)} is added to an accumulator and checked against
     * the max passed through the constructor.
     */
    public final class Traveled implements Range<Double> {

        private final double max;
        
        private double curr = 0;
        
        public Traveled(double max) {
            this.max = max;
        }
        
        @Override
        public boolean check(Double travelled) {
            if (travelled == null) return false;
            return (curr += travelled.doubleValue()) < max;
        }
        
    }
    
    /**
     * Implementation of Range that checks distance between two points.
     * <br><br>
     * The value given to {@link #check(Location)} is checked for distance from the
     * starting location passed through the constructor against the max.
     * <br><br>
     * Only the coordinates of the start location are stored, so the start location
     * does not need to be cloned before being passed to the constructor.
     */
    public final class Distance implements Range<Location> {
        
        private final double x, y, z, max;
        
        public Distance(Location start, double max) {
            this.x = start.getX();
            this.y = start.getY();
            this.z = start.getZ();
            this.max = max;
        }

        @Override
        public boolean check(Location loc) {
            double dx = loc.getX() - x;
            double dy = loc.getY() - y;
            double dz = loc.getZ() - z;
            return dx * dx + dy * dy + dz * dz < max;
        }
        
    }
}
