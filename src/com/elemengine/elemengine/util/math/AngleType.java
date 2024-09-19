package com.elemengine.elemengine.util.math;

public abstract class AngleType {

    /**
     * Converts the given angle from the given angle type to radians
     * 
     * @param angle value of the angle to convert
     * @return radian value of the angle
     */
    public abstract double toRadians(double angle);

    /**
     * Converts the given radians to the this angle type
     * 
     * @param angle value of the radians angle
     * @return angle value of this type
     */
    public abstract double fromRadians(double angle);

    /**
     * Converts the given angle from this type to the given other type
     * 
     * @param other the other angle type to convert to
     * @param angle value of the angle to convert
     * @return angle of the other value type
     */
    public final double convert(AngleType other, double angle) {
        return other.fromRadians(toRadians(angle));
    }

    public static final AngleType RADIAN = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle;
        }

        @Override
        public double fromRadians(double angle) {
            return angle;
        }

    };

    public static final AngleType DEGREE = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle * Math.PI / 180;
        }

        @Override
        public double fromRadians(double angle) {
            return angle * 180 / Math.PI;
        }

    };
    
    public static final AngleType MINUTE = new AngleType() {
        
        @Override
        public double toRadians(double angle) {
            return angle * Math.PI / 10800;
        }
        
        @Override
        public double fromRadians(double angle) {
            return angle * 10800 / Math.PI;
        }
    };
    
    public static final AngleType SECOND = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle * Math.PI / 648000;
        }

        @Override
        public double fromRadians(double angle) {
            return angle * 648000 / Math.PI;
        }
        
    };
    
    public static final AngleType GRADIAN = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle * Math.PI / 200;
        }

        @Override
        public double fromRadians(double angle) {
            return angle * 200 / Math.PI;
        }
        
    };
    
    public static final AngleType TURN = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle * 2 * Math.PI;
        }

        @Override
        public double fromRadians(double angle) {
            return angle / 2 / Math.PI;
        }
        
    };
    
    public static final AngleType BINARY = new AngleType() {

        @Override
        public double toRadians(double angle) {
            return angle * Math.PI / 128;
        }

        @Override
        public double fromRadians(double angle) {
            return angle * 128 / Math.PI;
        }
        
    };
}
