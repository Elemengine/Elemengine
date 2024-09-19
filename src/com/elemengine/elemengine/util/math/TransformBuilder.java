package com.elemengine.elemengine.util.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class TransformBuilder {

    public enum CenterMode {
        NORMAL, CENTER,
    }
    
    private final Vector3f translation = new Vector3f(), rotation = new Vector3f(), scale = new Vector3f(1.0f);
    private final CenterMode centering;
    
    private TransformBuilder(CenterMode centering) {
        this.centering = centering;
    }
    
    public TransformBuilder translate(float x, float y, float z) {
        translation.x += x;
        translation.y += y;
        translation.z += z;
        return this;
    }
    
    public TransformBuilder scale(float scalar) {
        scale.x *= scalar;
        scale.y *= scalar;
        scale.z *= scalar;
        return this;
    }
    
    public TransformBuilder scale(float x, float y, float z) {
        scale.x *= x;
        scale.y *= y;
        scale.z *= z;
        return this;
    }
    
    public TransformBuilder rotateX(float angle, AngleType type) {
        rotation.x += type.toRadians(angle);
        return this;
    }
    
    public TransformBuilder rotateY(float angle, AngleType type) {
        rotation.y += type.toRadians(angle);
        return this;
    }
    
    public TransformBuilder rotateZ(float angle, AngleType type) {
        rotation.z += type.toRadians(angle);
        return this;
    }
    
    public Matrix4f toMatrix() {
        Matrix4f transform = new Matrix4f()
                .translation(translation)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z)
                .scale(scale);
        
        if (centering == CenterMode.CENTER) {
            transform.translate(scale.mul(-0.5f, new Vector3f()));
        }
        
        return transform;
    }
    
    public static TransformBuilder normal() {
        return new TransformBuilder(CenterMode.NORMAL);
    }
    
    public static TransformBuilder centered() {
        return new TransformBuilder(CenterMode.CENTER);
    }
}
