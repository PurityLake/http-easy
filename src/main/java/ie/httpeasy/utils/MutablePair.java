package ie.httpeasy.utils;

public class MutablePair<T, U> {
    private T k;
    private U val;

    public MutablePair(T key, U value) {
        k = key;
        val = value;
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