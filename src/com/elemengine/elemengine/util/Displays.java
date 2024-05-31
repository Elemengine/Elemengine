package com.elemengine.elemengine.util;

import java.util.function.Consumer;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public class Displays {
    
    private Displays() {}

    public static <T extends Display> T transform(T display, Consumer<Transformation> transformer) {
        Transformation transform = display.getTransformation();
        transformer.accept(transform);
        display.setTransformation(transform);
        return display;
    }
    
    public static <T extends Display> T scale(T display, float scalar) {
        Transformation transform = display.getTransformation();
        transform.getScale().mul(scalar);
        display.setTransformation(transform);
        return display;
    }
    
    public static <T extends Display> T scale(T display, float x, float y, float z) {
        Transformation transform = display.getTransformation();
        transform.getScale().mul(x, y, z);
        display.setTransformation(transform);
        return display;
    }
    
    public static <T extends Display> T setScale(T display, float scalar) {
        Transformation transform = display.getTransformation();
        transform.getScale().set(scalar);
        display.setTransformation(transform);
        return display;
    }
    
    public static <T extends Display> T setScale(T display, float x, float y, float z) {
        Transformation transform = display.getTransformation();
        transform.getScale().set(x, y, z);
        display.setTransformation(transform);
        return display;
    }
}
