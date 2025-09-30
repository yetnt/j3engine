package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GObject;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.UUID;

public class Thing {
    private Vector3 anchor;
    private final UUID id;
    private ArrayDeque<GObject> objects = new ArrayDeque<>();

    public Thing(Renderer renderer, Layer l) {
        l = l == null ? renderer.layers.getFirst() : l;
        l.add(this);
        id = UUID.randomUUID();
    }

    /**
     * Adds one or more GObjects to this Thing.
     * @param gObjects The GObjects to add.
     */
    public Thing addObjs(GObject ...gObjects) {
        Collections.addAll(objects, gObjects);
        return this;
    }

    public void draw(Renderer renderer, Graphics2D graphics2D, Camera camera) {
        for (GObject o : objects) {
            o.draw(renderer, graphics2D, camera);
        }
    }

    public ArrayDeque<GObject> getObjects() {
        return objects;
    }
}
