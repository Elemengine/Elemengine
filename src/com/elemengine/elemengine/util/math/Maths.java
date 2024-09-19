package com.elemengine.elemengine.util.math;

public final class Maths {
    
    public static final double TAU = Math.PI * 2.0;
    public static final double HALF_PI = Math.PI / 2.0;
    public static final double QUARTER_PI = HALF_PI / 2.0;

    private Maths() {}

    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
    
    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
    
    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
    
    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }
    
    /**
     * Calculates the binomial coefficient, or "n choose k". It is the number of
     * unordered combinations of k elements picked from a group of n elements.
     * 
     * @param n size of the group
     * @param k number of elements to pick from the group (<= n)
     * @return the binomial coefficient
     */
    public static double nCk(int n, int k) {
        if (k == 0 || n == k) {
            return 1.0;
        }

        double result = 1.0;
        for (int i = 1; i <= k; ++i) {
            result *= (n + 1 - i) / (double) i;
        }

        return result;
    }
}
