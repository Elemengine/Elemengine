package com.elemengine.elemengine.util.reflect;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public final class Fields {
    
    private Fields() {}
    
    public static Optional<Object> get(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            Object value = null;
            boolean access = field.canAccess(obj);
            field.setAccessible(true);
            
            value = field.get(obj);
            
            field.setAccessible(access);
            return Optional.ofNullable(value);
        } catch (Exception e) {}
        
        return Optional.empty();
    }
    
    public static Optional<Object> get(Object obj, Field field) {
        Object value = null;
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        
        try {
            value = field.get(obj);
        } catch (Exception e) {
            value = null;
        }
        
        field.setAccessible(access);
        return Optional.ofNullable(value);
    }
    
    public static <T> Optional<T> getAs(Object obj, String name, Class<T> clazz) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            T value = null;
            boolean access = field.canAccess(obj);
            field.setAccessible(true);
            
            value = clazz.cast(field.get(obj));
            
            field.setAccessible(access);
            return Optional.ofNullable(value);
        } catch (Exception e) {}
        
        return Optional.empty();
    }
    
    public static <T> Optional<T> getAs(Object obj, Field field, Class<T> clazz) {
        T value = null;
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        
        try {
            value = clazz.cast(field.get(obj));
        } catch (Exception e) {
            value = null;
        }
        
        field.setAccessible(access);
        return Optional.ofNullable(value);
    }
    
    public static void getSet(Object obj, Field field, Function<Object, Object> mod) {
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        
        try {
            field.set(obj, mod.apply(field.get(obj)));
        } catch (Exception e) {}
        
        field.setAccessible(access);
    }
    
    public static <T> void getSetAs(Object obj, Field field, Class<T> clazz, Function<T, Object> mod) {
        if (!field.getType().isAssignableFrom(clazz)) {
            return;
        }
        
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        
        try {
            field.set(obj, mod.apply(clazz.cast(field.get(obj))));
        } catch (Exception e) {}
        
        field.setAccessible(access);
    }
    
    public static void set(Object obj, Field field, Object value) {
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        
        try {
            field.set(obj, value);
        } catch (Exception e) {}
        
        field.setAccessible(access);
    }
}
