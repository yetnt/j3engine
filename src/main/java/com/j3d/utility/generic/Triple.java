package com.j3d.utility.generic;

public class Triple<T> {
    final T first;
    final T second;
    final T third;

    public Triple(T a, T b, T c) {
        first = a;
        second = b;
        third = c;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }
}
