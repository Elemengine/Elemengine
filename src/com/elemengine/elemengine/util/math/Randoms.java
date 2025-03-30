package com.elemengine.elemengine.util.math;

import java.util.concurrent.ThreadLocalRandom;

public final class Randoms {

    private Randoms() {}
    
    public static <T> T arrayElement(T[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }
}
