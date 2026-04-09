package com.j3d.engine.geometry.geo2d;

public interface HasParent<T> {
    T getParent();
    void setParent(T parent);
    boolean hasParent();
}
