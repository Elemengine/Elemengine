package com.elemengine.elemengine.util.data;

public final class Reducers {

    private Reducers() {}
    
    public static String commaString(String base, String next) {
        return base + ", " + next;
    }
}
