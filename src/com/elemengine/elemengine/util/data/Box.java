package com.elemengine.elemengine.util.data;

import java.util.function.Function;

public class Box<T> {

    private T held;

    Box(T obj) {
        this.held = obj;
    }

    public T get() {
        return held;
    }

    public void set(T obj) {
        this.held = obj;
    }
    
    public void update(Function<T, T> func) {
        this.held = func.apply(held);
    }

    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }
}
