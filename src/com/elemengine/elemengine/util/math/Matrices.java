package com.elemengine.elemengine.util.math;

import org.bukkit.util.Vector;
import org.joml.Matrix4f;

public final class Matrices {

    private Matrices() {}

    public static Matrix4f align(Vector toward, Matrix4f dest) {
        float pitch = (float) -Math.atan2(toward.getY(), Math.sqrt(toward.getX()*toward.getX() + toward.getZ()*toward.getZ()));
        float yaw = (float) Math.atan2(toward.getX(), toward.getZ());
        
        return dest.rotateY(yaw).rotateX(pitch);
    }
    
    public static Matrix4f aligned(Vector toward) {
        return align(toward, new Matrix4f());
    }
}
