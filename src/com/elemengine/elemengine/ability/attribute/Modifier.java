package com.elemengine.elemengine.ability.attribute;

@FunctionalInterface
public interface Modifier {

    /**
     * Returns the empty modifier that just returns the object it is given
     */
    public static Modifier EMPTY = (o) -> o;

    /**
     * Takes in a value and does something with it. Since the given value is an
     * Object, you must check if the value is an <code>instanceof</code> the data
     * type you wish to work with, and if the value is not then just return the
     * value unmodified.
     * 
     * @param value The value taken in
     * @return The modified value
     */
    public Object apply(Object value);

    /**
     * Composes the modifiers together such that this modifier is applied and then
     * the <code>other</code> modifier is applied.
     * 
     * @param other The other {@link Modifier} to compose with
     * @return new {@link Modifier}
     */
    public default Modifier and(Modifier other) {
        if (other == null) {
            return this;
        }

        return (val) -> other.apply(this.apply(val));
    }

    /**
     * Returns a premade modifier for doing multiplication
     * 
     * @param modifier what to multiply by
     * @return multiplication modifier
     */
    public static Modifier multiply(Number modifier) {
        return (x) -> {
            if (x.getClass() == Double.TYPE) {
                return (double) x * modifier.doubleValue();
            } else if (x.getClass() == Float.TYPE) {
                return (float) x * modifier.floatValue();
            } else if (x.getClass() == Long.TYPE) {
                return (long) x * modifier.longValue();
            } else if (x.getClass() == Integer.TYPE) {
                return (int) x * modifier.intValue();
            } else if (x.getClass() == Short.TYPE) {
                return (short) x * modifier.shortValue();
            } 
            
            return x;
        };
    }

    /**
     * Returns a premade modifier for doing addition
     * 
     * @param modifier what to add by
     * @return addition modifier
     */
    public static Modifier add(Number modifier) {
        return (x) -> {
            if (x.getClass() == Double.TYPE) {
                return (double) x + modifier.doubleValue();
            } else if (x.getClass() == Float.TYPE) {
                return (float) x + modifier.floatValue();
            } else if (x.getClass() == Long.TYPE) {
                return (long) x + modifier.longValue();
            } else if (x.getClass() == Integer.TYPE) {
                return (int) x + modifier.intValue();
            } else if (x.getClass() == Short.TYPE) {
                return (short) x + modifier.shortValue();
            } 
            
            return x;
        };
    }

    /**
     * Returns a premade modifier for doing subtraction
     * 
     * @param modifier what to subtract by
     * @return subtraction modifier
     */
    public static Modifier subtract(Number modifier) {
        return (x) -> {
            if (x.getClass() == Double.TYPE) {
                return (double) x - modifier.doubleValue();
            } else if (x.getClass() == Float.TYPE) {
                return (float) x - modifier.floatValue();
            } else if (x.getClass() == Long.TYPE) {
                return (long) x - modifier.longValue();
            } else if (x.getClass() == Integer.TYPE) {
                return (int) x - modifier.intValue();
            } else if (x.getClass() == Short.TYPE) {
                return (short) x - modifier.shortValue();
            } 
            
            return x;
        };
    }

    /**
     * Returns a premade modifier for doing division. The modifier given by this
     * method will return the input value in the case that the modifier is zero.
     * 
     * @param modifier what to divide by
     * @return division modifier
     */
    public static Modifier divide(Number modifier) {
        return (x) -> {
            if (modifier.doubleValue() == 0.0) {
                return x;
            } else if (x.getClass() == Double.TYPE) {
                return (double) x / modifier.doubleValue();
            } else if (x.getClass() == Float.TYPE) {
                return (float) x / modifier.floatValue();
            } else if (x.getClass() == Long.TYPE) {
                return (long) x / modifier.longValue();
            } else if (x.getClass() == Integer.TYPE) {
                return (int) x / modifier.intValue();
            } else if (x.getClass() == Short.TYPE) {
                return (short) x / modifier.shortValue();
            }
            
            return x;
        };
    }
    
    /**
     * Returns a premade modifier for doing percents. 
     * @param percent percent to take, from 0 to 100
     * @return percent modifier
     */
    public static Modifier percent(Number percent) {
        return x -> {
            if (x.getClass() == Double.TYPE) {
                return (double) x * percent.doubleValue() / 100.0;
            } else if (x.getClass() == Float.TYPE) {
                return (float) x * percent.floatValue() / 100.0f;
            } else if (x.getClass() == Long.TYPE) {
                return (long) x * percent.longValue() / 100l;
            } else if (x.getClass() == Integer.TYPE) {
                return (int) x * percent.intValue() / 100;
            } else if (x.getClass() == Short.TYPE) {
                return (short) x * percent.shortValue() / 100;
            }
            
            return x;
        };
    }
}
