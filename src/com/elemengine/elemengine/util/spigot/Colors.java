package com.elemengine.elemengine.util.spigot;

import org.bukkit.Color;

public final class Colors {

    private Colors() {}
    
    public static Color interpolate(Color left, Color right, double amount) {
        int[] rgba = toRGBA(left);
        int[] next = toRGBA(right);
        
        for (int i = 0; i < 4; ++i) {
            rgba[i] = (int) Math.max(0, Math.min(255, rgba[i] + amount * (next[i] - rgba[i])));
        }
        
        return Colors.fromRGBA(rgba);
    }
    
    public static int[] toRGBA(Color color) {
        return new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
    }
    
    public static Color copy(Color color) {
        return Color.fromARGB(color.asARGB());
    }
    
    public static Color fromRGBA(int[] rgba) {
        return Color.fromARGB(rgba[3], rgba[0], rgba[1], rgba[2]);
    }
    
    public static Color fromHSV(double hue, double sat, double val) {
        double angle = hue / 60;
        double chroma = sat * val;
        
        double xVal = chroma * (1.0 - Math.abs(angle % 2 - 1));
        
        int x;
        int c;
        
        if (angle < 1) {
            c = 0;
            x = 1;
        } else if (angle < 2) {
            x = 0;
            c = 1;
        } else if (angle < 3) {
            c = 1;
            x = 2;
        } else if (angle < 4) {
            x = 1;
            c = 2;
        } else if (angle < 5) {
            x = 0;
            c = 2;
        } else {
            x = 2;
            c = 0;
        }
        
        int[] rgb = {0, 0, 0};
        rgb[x] = (int) (256.0 * xVal);
        rgb[c] = (int) (chroma * 256.0);
        
        return Color.fromRGB(rgb[0], rgb[1], rgb[2]);
    }
}
