package com.j3d.engine.geometry;

import com.j3d.engine.geometry.base.CartesianPoint;

import java.awt.*;
import java.util.*;

public class GObject {
    private CartesianPoint pivot;
    private final String Id;

    public GObject() {
        Id = UUID.randomUUID().toString();
    }

    public CartesianPoint getPivot() {
        return pivot;
    }

    public void setPivot(CartesianPoint pivot) {
        this.pivot = pivot;
    }

    public String getId() {
        return Id;
    }

    public ArrayList<Object> toArray() {
        return new ArrayList<>(Arrays.asList(getId(), getPivot().toArray()));
    }
}
