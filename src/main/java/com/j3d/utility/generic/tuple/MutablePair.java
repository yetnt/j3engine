package com.j3d.utility.generic.tuple;

public class MutablePair<T, U> {
    private T first;
    private U second;

    public MutablePair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }
    public U getSecond() {
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }
    public void setSecond(U second) {
        this.second = second;
    }
}
