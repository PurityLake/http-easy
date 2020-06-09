package ie.httpeasy.utils;

public class MutablePair<T, U> {
    private T k;
    private U val;

    public MutablePair(T key, U value) {
        k = key;
        val = value;
    }

    public boolean equals(MutablePair<T, U> other) {
        return k.equals(other.k) && val.equals(other.val);
    }

    public T key() {
        return k;
    }
    public void key(T key) {
        k = key;
    }

    public U value() {
        return val;
    }
    public void value(U value) {
        val = value;
    }
}