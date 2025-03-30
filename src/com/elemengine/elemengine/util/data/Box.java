package com.elemengine.elemengine.util.data;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Predicate;

public class Box<T> {

    private T held;

    Box(T obj) {
        this.held = obj;
    }

    public T get() {
        return this.held;
    }

    public void set(T obj) {
        this.held = obj;
    }
    
    public boolean check(Predicate<T> test) {
        return test.apply(this.held);
    }
    
    public void alter(Consumer<T> func) {
        func.accept(this.held);
    }
    
    public void update(Function<T, T> func) {
        this.held = func.apply(this.held);
    }
    
    public <V> V into(Function<T, V> func) {
        return func.apply(this.held);
    }

    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }
    
    public static <T> Box<T> empty() {
        return new Box<>(null);
    }
}
