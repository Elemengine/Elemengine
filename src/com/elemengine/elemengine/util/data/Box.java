package com.elemengine.elemengine.util.data;

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

    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }
}
