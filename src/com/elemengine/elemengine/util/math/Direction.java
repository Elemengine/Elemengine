package com.elemengine.elemengine.util.math;

import org.bukkit.Location;

public class Direction {

    private double theta, cosTheta, sinTheta;
    private double phi, cosPhi, sinPhi;
    
    public Direction(AngleType type, double theta, double phi) {
        this.theta = theta;
        this.phi = phi;
    }
    
    public double getTheta() {
        return theta;
    }
    
    public double getPhi() {
        return phi;
    }
    
    public Direction set(AngleType type, double theta, double phi) {
        this.theta = type.toRadians(theta);
        this.cosTheta = Math.cos(theta);
        this.sinTheta = Math.sin(theta);
        
        this.phi = type.toRadians(phi);
        this.cosPhi = Math.cos(phi);
        this.sinPhi = -Math.sin(phi);
        
        return this;
    }
    
    public Location addTo(Location loc, double magnitude) {
        double x = magnitude * sinTheta * -cosPhi;
        double y = magnitude * sinPhi;
        double z = magnitude * cosTheta * cosPhi;
        
        return loc.add(x, y, z);
    }
}
