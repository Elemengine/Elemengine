package com.elemengine.elemengine.util.math;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;

import com.elemengine.elemengine.util.data.Tuple;
import com.elemengine.elemengine.util.spigot.Colors;

public class Gradient {

    private final List<Tuple<Double, Color>> colors;
    
    public Gradient(List<Tuple<Double, Color>> colors) {
        this.colors = colors;
    }
    
    public Color getColor(double percent) {
        percent = Maths.clamp(percent, 0, 1);
        int right = 1;
        
        while (right < colors.size() - 1) {
            Tuple<Double, Color> section = colors.get(right);
            if (section.left >= percent) {
                break;
            }
            
            ++right;
        }
        
        Tuple<Double, Color> leftColor = colors.get(right - 1);
        Tuple<Double, Color> rightColor = colors.get(right);
        
        double adjusted = (percent - leftColor.left) / (rightColor.left - leftColor.left);
        
        return Colors.interpolate(leftColor.right, rightColor.right, adjusted);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        final List<Tuple<Color, Double>> colors = new ArrayList<>();
        int amount = 0;
        
        Builder() {}
        
        public Builder add(Color color) {
            return this.add(color, 1);
        }
        
        public Builder add(Color color, int amount) {
            this.colors.add(Tuple.of(color, (double) amount));
            this.amount += amount;
            return this;
        }
        
        public Gradient end(Color last) {
            List<Tuple<Double, Color>> gradient = new ArrayList<>();
            
            double acc = 0;
            for (Tuple<Color, Double> color : this.colors) {
                gradient.add(Tuple.of(acc, color.left));
                acc += color.right / this.amount;
            }
            
            gradient.add(Tuple.of(1d, last));
            
            return new Gradient(gradient);
        }
    }
}
