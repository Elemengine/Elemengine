package com.elemengine.elemengine.util.math;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class Vectors {

    public static final ImmutableVector RIGHT = new ImmutableVector(1.0, 0.0, 0.0);
    public static final ImmutableVector UP = new ImmutableVector(0.0, 1.0, 0.0);
    public static final ImmutableVector FORWARD = new ImmutableVector(0.0, 0.0, 1.0);
    public static final ImmutableVector LEFT = new ImmutableVector(-1.0, 0.0, 0.0);
    public static final ImmutableVector DOWN = new ImmutableVector(0.0, -1.0, 0.0);
    public static final ImmutableVector BACKWARD = new ImmutableVector(0.0, 0.0, -1.0);
    
    public static final double SQUARED_EPSILON = Vector.getEpsilon() * Vector.getEpsilon();

    private Vectors() {}

    /**
     * Calculates the vector pointing from the start location to the end location, with length
     * equal to the distance between the two locations
     * @param start {@link Location} to start at
     * @param end {@link Location} to end at
     * @return vector directed from start to end, with length of the distance between the locations
     */
    public static Vector direction(Location start, Location end) {
        return new Vector(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
    }

    /**
     * Calculates the vector pointing in the direction described by the given yaw and pitch,
     * with length 1
     * @param type Type of the angles given
     * @param yaw horizontal angle
     * @param pitch vertical angle
     * @return vector in the direction of yaw and pitch, with length 1
     */
    public static Vector direction(AngleType type, float yaw, float pitch) {
        double yawRad = type.toRadians(yaw), pitchRad = type.toRadians(pitch);
        double xz = Math.cos(pitchRad);
        return new Vector(-xz * Math.sin(yawRad), -Math.sin(pitchRad), xz * Math.cos(yawRad));
    }
    
    /**
     * Calculates the vector pointing in the direction described by the given yaw and pitch,
     * with length 1
     * @param type Type of the angles given
     * @param yaw horizontal angle
     * @param pitch vertical angle
     * @return vector in the direction of yaw and pitch, with length 1
     */
    public static Vector direction(AngleType type, double yaw, double pitch) {
        double yawRad = type.toRadians(yaw), pitchRad = type.toRadians(pitch);
        double xz = Math.cos(pitchRad);
        return new Vector(-xz * Math.sin(yawRad), -Math.sin(pitchRad), xz * Math.cos(yawRad));
    }

    /**
     * Gets the pitch of the given vector
     * 
     * @param vector a vector
     * @return pitch of vector
     */
    public static float getPitch(Vector vector, AngleType angle) {
        double x = vector.getX();
        double z = vector.getZ();

        if (x == 0 && z == 0) {
            return vector.getY() > 0 ? (float) angle.fromRadians(-Math.PI / 2) : (float) angle.fromRadians(Math.PI / 2);
        }

        double pitch = Math.atan(-vector.getY() / Math.sqrt(x * x + z * z));
        return (float) angle.fromRadians(pitch);
    }

    /**
     * Gets the yaw of the given vector, a vector pointing straight up or down will
     * give a yaw of 0
     * 
     * @param vector a vector
     * @return yaw of the vector
     */
    public static float getYaw(Vector vector, AngleType angle) {
        double yaw = (Math.atan2(-vector.getX(), vector.getZ()) + 2 * Math.PI) % (2 * Math.PI);
        return (float) angle.fromRadians(yaw);
    }
    
    /**
     * Will normalize the vector so long as its squared length is greater than
     * {@link Vector#getEpsilon()} squared, otherwise returns the unmodified vector.
     * @param v Vector to normalize
     * @return normalized vector if possible, always the same vector object
     */
    public static Vector normalize(Vector v) {
        if (v.lengthSquared() > SQUARED_EPSILON) {
            v.normalize();
        }
        
        return v;
    }
    
    /**
     * Calculates a vector that is orthogonal to the axis vector described by the given yaw and pitch. 
     * This orthogonal vector will always point to the right-hand side when looking in the direction 
     * described by the yaw and pitch.
     * @param type Type of the angles given
     * @param yaw horizontal angle
     * @param pitch vertical angle
     * @return a vector orthogonal to the yaw and pitch
     */
    public static Vector orthogonal(AngleType type, double yaw, double pitch) {
        return orthogonal(type, yaw, pitch, 0);
    }

    /**
     * Calculates a vector that is orthogonal to the axis vector described by the given yaw and pitch. 
     * This orthogonal vector will always point to the right-hand side when looking in the direction 
     * described by the yaw and pitch.
     * @param type Type of the angles given
     * @param yaw horizontal angle
     * @param pitch vertical angle
     * @param rotation angle to rotate the orthogonal vector around the axis
     * @return a vector orthogonal to the yaw and pitch and rotated around the axis
     */
    public static Vector orthogonal(AngleType type, double yaw, double pitch, double rotation) {
        Vector axis = direction(type, yaw, pitch);
        Vector other = direction(type, yaw, pitch - Math.PI / 2);
    
        return rotate(axis.getCrossProduct(other).normalize(), axis, type, rotation);
    }
    
    public static Vector random2D(AngleType type, double yaw, double pitch) {
        double theta = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
        
        Vector planeX = orthogonal(type, yaw, pitch);
        Vector planeY = orthogonal(type, yaw, pitch, type.fromRadians(Math.PI / 2));
        
        return planeX.multiply(Math.cos(theta)).add(planeY.multiply(Math.sin(theta)));
    }
    
    public static Vector random3D() {
        double theta = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
        double y = ThreadLocalRandom.current().nextDouble(2) - 1;
        double xz = Math.sqrt(1 - y * y);
        
        return new Vector(xz * Math.cos(theta), y, xz * Math.sin(theta));
    }
    
    public static Vector3f random3F() {
        double theta = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
        double y = ThreadLocalRandom.current().nextDouble(2) - 1;
        double xz = Math.sqrt(1 - y * y);
        
        return new Vector3f((float) (xz * Math.sin(theta)), (float) y, (float) (xz * Math.cos(theta)));
    }

    public static RayTraceResult rayTrace(Location start, double maxDistance, FluidCollisionMode fluids, boolean ignorePassable, double raySize, Predicate<Entity> filter) {
        return start.getWorld().rayTrace(start, start.getDirection(), maxDistance, fluids, ignorePassable, raySize, filter);
    }

    public static RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluids, boolean ignorePassable, double raySize, Predicate<Entity> filter) {
        return start.getWorld().rayTrace(start, direction, maxDistance, fluids, ignorePassable, raySize, filter);
    }

    public static RayTraceResult rayTraceBlocks(Location start, double maxDistance, FluidCollisionMode fluids, boolean ignorePassable) {
        return start.getWorld().rayTraceBlocks(start, start.getDirection(), maxDistance, fluids, ignorePassable);
    }

    public static RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluids, boolean ignorePassable) {
        return start.getWorld().rayTraceBlocks(start, direction, maxDistance, fluids, ignorePassable);
    }
    
    /**
     * Rotate a vector around an axis, this does mutate the rotator
     * 
     * @param rotator  vector to rotate
     * @param axis     axis to rotate around
     * @param rotation angle to rotate in radians
     * @return rotated vector, <b>not a new vector</b>
     */
    public static Vector rotate(Vector rotator, Vector axis, AngleType angle, double rotation) {
        return rotator.rotateAroundAxis(axis.clone().normalize(), angle.toRadians(-rotation));
    }

    /**
     * Overrides mutation methods to be immutable. Any method that returns "the same vector"
     * will actually return a new vector with the appropriate operation done to it.
     */
    public static class ImmutableVector extends Vector {

        public ImmutableVector(double x, double y, double z) {
            super(x, y, z);
        }

        @Override
        public Vector add(Vector other) {
            return new Vector(x + other.getX(), y + other.getY(), z + other.getZ());
        }

        @Override
        public Vector subtract(Vector other) {
            return new Vector(x - other.getX(), y - other.getY(), z - other.getZ());
        }

        @Override
        public Vector multiply(Vector other) {
            return new Vector(x * other.getX(), y * other.getY(), z * other.getZ());
        }

        @Override
        public Vector divide(Vector other) {
            return new Vector(x / other.getX(), y / other.getY(), z / other.getZ());
        }

        @Override
        public Vector copy(Vector other) {
            return other.clone();
        }

        @Override
        public Vector midpoint(Vector other) {
            return this.clone().midpoint(other);
        }

        @Override
        public Vector multiply(int m) {
            return new Vector(x * m, y * m, z * m);
        }

        @Override
        public Vector multiply(float m) {
            return new Vector(x * m, y * m, z * m);
        }

        @Override
        public Vector multiply(double m) {
            return new Vector(x * m, y * m, z * m);
        }

        @Override
        public Vector crossProduct(Vector other) {
            return this.clone().crossProduct(other);
        }

        @Override
        public Vector normalize() {
            return new Vector(x, y, z).normalize();
        }

        @Override
        public Vector rotateAroundX(double angle) {
            return this.clone().rotateAroundX(angle);
        }

        @Override
        public Vector rotateAroundY(double angle) {
            return this.clone().rotateAroundY(angle);
        }

        @Override
        public Vector rotateAroundZ(double angle) {
            return this.clone().rotateAroundZ(angle);
        }

        @Override
        public Vector rotateAroundAxis(Vector axis, double angle) {
            return this.clone().rotateAroundAxis(axis, angle);
        }

        @Override
        public Vector rotateAroundNonUnitAxis(Vector axis, double angle) {
            return this.clone().rotateAroundNonUnitAxis(axis, angle);
        }

        @Override
        public Vector setX(int x) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setX(float x) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setX(double x) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setY(int y) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setY(float y) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setY(double y) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setZ(int z) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setZ(float z) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector setZ(double z) {
            return new Vector(x, y, z);
        }

        @Override
        public Vector clone() {
            return new Vector(x, y, z);
        }
    }
}
