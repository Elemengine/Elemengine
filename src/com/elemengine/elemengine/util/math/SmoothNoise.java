package com.elemengine.elemengine.util.math;

import java.util.concurrent.ThreadLocalRandom;

public class SmoothNoise {
    
    private final double[][][] grid;
    
    public SmoothNoise(int size) {
        this.grid = new double[size][size][2];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                double angle = ThreadLocalRandom.current().nextDouble(Maths.TAU);
                grid[x][y] = new double[] {Math.cos(angle), Math.sin(angle)};
            }
        }
    }
    
    public double get(double x, double y) {
        double[][] corners = getCorners(x, y);
        
        double dx = x - Math.floor((float) x);
        double dy = y - Math.floor((float) y);
        
        double botLeft = dotProduct(dx, dy, corners[0]);
        double botRight = dotProduct(dx - 1, dy, corners[1]);
        double topLeft = dotProduct(dx, dy - 1, corners[2]);
        double topRight = dotProduct(dx - 1, dy - 1, corners[3]);
        
        double adjustedX = fade(dx);
        
        double interpXBot = interpolate(botLeft, botRight, adjustedX);
        double interpXTop = interpolate(topLeft, topRight, adjustedX);
      
        return fade((1 + interpolate(interpXBot, interpXTop, fade(dy))) / 2);
    }
    
    private double[][] getCorners(double x, double y) {
        int fx = ((((int) Math.floor(x)) % grid.length) + grid.length) % grid.length;
        int cx = (fx + 1) % grid.length;
        
        int fy = ((((int) Math.floor(y)) % grid.length) + grid.length) % grid.length;
        int cy = (fy + 1) % grid.length;
        
        return new double[][] { grid[fx][fy], grid[cx][fy], grid[fx][cy], grid[cx][cy] };
    }
    
    private double dotProduct(double vx, double vy, double[] v) {
      return vx * v[0] + vy * v[1];
    }
    
    private double fade(double z) {
      //return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
      return z * z * z * (z * (z * 6.0 - 15.0) + 10.0);
    }
    
    private double interpolate(double left, double right, double percent) {
      return left + (right - left) * percent;
    }
}
