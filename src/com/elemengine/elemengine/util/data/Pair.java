package com.elemengine.elemengine.util.data;

public class Pair<T> {

    public final T left, right;

    public Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + ", " + right.toString();
    }

    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Pair)) {
            return false;
        }

        Pair<?> pair = (Pair<?>) other;
        return this.left.equals(pair.left) && this.right.equals(pair.right) || this.left.equals(pair.right) && this.right.equals(pair.left);
    }

    public static <T> Pair<T> of(T left, T right) {
        return new Pair<>(left, right);
    }
}
