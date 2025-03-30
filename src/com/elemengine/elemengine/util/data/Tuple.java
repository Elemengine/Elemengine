package com.elemengine.elemengine.util.data;

public class Tuple<T, S> {

    public T left;
    public S right;
    
    private Tuple(T left, S right) {
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
        if (!(other instanceof Tuple)) {
            return false;
        }

        Tuple<?, ?> pair = (Tuple<?, ?>) other;
        return this.left.equals(pair.left) && this.right.equals(pair.right) || this.left.equals(pair.right) && this.right.equals(pair.left);
    }
    
    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }
}
