package com.elemengine.elemengine.ability.component;

public class Duration {
    
    private final double ticks;
    private double rem;
    
    public Duration(double ticks) {
        this.ticks = ticks;
        this.rem = ticks;
    }
    
    public boolean tick(double deltaTime) {
        return (rem -= deltaTime) > 0;
    }
    
    public double elapsed() {
        return ticks - rem;
    }
    
    public double elapsedPercent() {
        return 1.0 - rem / ticks;
    }
}
