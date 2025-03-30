package com.elemengine.elemengine.util.data;

import java.util.function.Supplier;

public final class Suppliers {
    
    private Suppliers() {}
    
    public static final Supplier<Boolean> TRUE = () -> true;
    public static final Supplier<Boolean> FALSE = () -> false;
}
