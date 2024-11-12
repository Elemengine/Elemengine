package com.elemengine.elemengine.util.spigot;

import java.util.function.Consumer;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public final class Displays {

    private Displays() {}
    
    public static void transform(Display entity, int delay, int duration, Consumer<Transformation> transformer) {
        Displays.transform(entity, transformer);
        entity.setInterpolationDuration(delay);
        entity.setInterpolationDuration(duration);
    }
    
    public static void transform(Display entity, Consumer<Transformation> transformer) {
        Transformation transform = entity.getTransformation();
        transformer.accept(transform);
        entity.setTransformation(transform);
    }
}
