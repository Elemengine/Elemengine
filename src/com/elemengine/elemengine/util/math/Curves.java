package com.elemengine.elemengine.util.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

public final class Curves {

    private Curves() {}

    /**
     * Calculates the general nth order bezier curve, where n is the length of the
     * points array minus 1. For values of n = 1, 2, 3; it is recommended to use the
     * specific function, like
     * {@link Curves#bezierQuadratic(Location, Location, Location, double)} instead
     * for better performance. Does not modify any given locations, so they do not
     * need to be cloned.
     * 
     * @param points Control points for the bezier curve
     * @param t      how far along the curve to go, from 0 to 1
     * @return a vector representation of the calculated point on the curve
     */
    public static Vector bezier(Location[] points, double t) {
        Preconditions.checkArgument(points.length > 0, "Cannot perform bezier curve with no points");
        t = Math.min(1, Math.max(0, t));

        int n = points.length - 1;
        double x = 0, y = 0, z = 0;
        for (int i = 0; i <= n; ++i) {
            double factor = Maths.nCk(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i);

            x += factor * points[i].getX();
            y += factor * points[i].getY();
            z += factor * points[i].getZ();
        }

        return new Vector(x, y, z);
    }

    /**
     * Linearly interpolates between the two given points (equivalent to a linear
     * bezier curve). Does not modify any given locations, so they do not need to be
     * cloned.
     * 
     * @param a starting point
     * @param b ending point
     * @param t how far between the points to interpolate
     * @return a vector representation of the calculated point on the curve
     */
    public static Vector lerp(Location a, Location b, double t) {
        t = Math.min(1, Math.max(0, t));

        return new Vector((1 - t) * a.getX() + t * b.getX(), (1 - t) * a.getY() + t * b.getY(), (1 - t) * a.getZ() + t * b.getZ());
    }

    /**
     * Calculates the point along a quadratic bezier curve, more efficiently than
     * the general function. Does not modify any given locations, so they do not
     * need to be cloned.
     * 
     * @param a starting point
     * @param b control point
     * @param c ending point
     * @param t how far along the curve to go, from 0 to 1
     * @return a vector representation of the calculated point on the curve
     */
    public static Vector bezierQuadratic(Location a, Location b, Location c, double t) {
        t = Math.min(1, Math.max(0, t));
    
        double tSquare = t * t;
        double tInvert = (1 - t) * (1 - t);
    
        return new Vector(
            b.getX() + tInvert * (a.getX() - b.getX()) + tSquare * (c.getX() - b.getX()), 
            b.getY() + tInvert * (a.getY() - b.getY()) + tSquare * (c.getY() - b.getY()), 
            b.getZ() + tInvert * (a.getZ() - b.getZ()) + tSquare * (c.getZ() - b.getZ())
        );
    }

    /**
     * Calculates the point along a cubic bezier curve, more efficiently than the
     * general function. Does not modify any given locations, so they do not need to
     * be cloned.
     * 
     * @param a starting point
     * @param b first control point
     * @param c second control point
     * @param d ending point
     * @param t how far along the curve to go, from 0 to 1
     * @return a vector representation of the calculated point on the curve
     */
    public static Vector bezierCubic(Location a, Location b, Location c, Location d, double t) {
        t = Math.min(1, Math.max(0, t));

        double tSquare = t * t;
        double tInvert = (1 - t) * (1 - t);

        return new Vector(tInvert * (1 - t) * a.getX() + 3 * tInvert * t * b.getX() + 3 * (1 - t) * tSquare * c.getX() + tSquare * t * d.getX(), tInvert * (1 - t) * a.getY() + 3 * tInvert * t * b.getY() + 3 * (1 - t) * tSquare * c.getY() + tSquare * t * d.getY(),
                tInvert * (1 - t) * a.getZ() + 3 * tInvert * t * b.getZ() + 3 * (1 - t) * tSquare * c.getZ() + tSquare * t * d.getZ());
    }
}
