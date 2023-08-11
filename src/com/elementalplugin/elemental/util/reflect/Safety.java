package com.elementalplugin.elemental.util.reflect;

import java.lang.reflect.Field;
import java.util.function.Function;

public final class Safety {

    private Object obj;
    private Field field;

    public Safety(Object obj, Field field) {
        this.obj = obj;
        this.field = field;
    }

    public Object get() {
        Object o = null;

        try {
            boolean a = field.canAccess(obj);
            field.setAccessible(true);
            o = field.get(obj);
            field.setAccessible(a);
        } catch (Exception e) {}

        return o;
    }

    public <T> T getAs(Class<T> clazz) {
        Object o = this.get();

        if (field.getType().isAssignableFrom(clazz)) {
            return clazz.cast(o);
        }

        return null;
    }

    public void set(Object value) {
        try {
            boolean access = field.canAccess(obj);
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(access);
        } catch (Exception e) {}
    }

    public void getSet(Function<Object, Object> modify, boolean ignoreNull) {
        Object o = this.get();

        if (!ignoreNull && o == null) {
            return;
        }

        this.set(modify.apply(o));
    }

    public <T> void getAsSet(Class<T> clazz, Function<T, Object> modify, boolean ignoreNull) {
        T o = this.getAs(clazz);

        if (!ignoreNull && o == null) {
            return;
        }

        this.set(modify.apply(o));
    }
}
