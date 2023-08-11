package com.elementalplugin.elemental.util.data;

import java.util.HashSet;

public class PairSet<T> extends HashSet<Pair<T>> {

    private static final long serialVersionUID = 9064677383638093874L;

    public boolean add(T left, T right) {
        return add(Pair.of(left, right));
    }

    public boolean remove(T left, T right) {
        return remove(Pair.of(left, right));
    }

    public boolean contains(T left, T right) {
        return contains(Pair.of(left, right));
    }
}
