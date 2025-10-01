package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;

import java.awt.*;
import java.util.*;

/**
 * Represents a 3D object composed of multiple 2D geometric objects (GObjects).
 */
public class Thing {

    /** The centroid of the Thing, calculated from the GPoints it contains. */
    private Vector3 centroid;

    /** The unique identifier for this Thing. */
    private final UUID id;

    /** The list of 2D geometric objects that compose this 3D Thing. */
    private final ArrayList<GObject> objects = new ArrayList<>();

    /** A Flag set by the Thing itself to check whether its part of the background. if so it only draws the
     * axes and the background.
     */
    private boolean isBg = false;

    public Thing(Renderer renderer, Layer l) {
        l = l == null ? renderer.layers.get(1) : l;
        if (l.getIdentifier().equals(Layer.backgroundId)) {
            isBg = true;
        }
        l.add(this);
        id = UUID.randomUUID();
    }

    /**
     * Adds one or more GObjects to this Thing.
     * @param gObjects The GObjects to add.
     */
    public Thing addObjs(GObject ...gObjects) {
        Collections.addAll(objects, gObjects);
        ArrayList<Vector3> pts = new ArrayList<>();
        for (GObject ob : gObjects) {
            if (ob instanceof GPoint p) {
                pts.add(p.getPivot());
            }
        }
        Vector3 sum = Vector3.reduce(pts, Vector3::add);
        centroid = sum.div(pts.size());
        return this;
    }

    public void draw(Renderer renderer, Graphics2D graphics2D, Camera camera) {
        if (isBg) {
            graphics2D.setColor(new Color(52, 52, 52));
            graphics2D.fillRect(0, 0, renderer.screenSize.width, renderer.screenSize.height);
            renderer.axis(graphics2D, camera);
            return;
        }
        objects.sort(Comparator.comparingDouble(o -> o.getPivot().distance(camera.getPosition())));
        for (GObject o : objects.reversed()) {
            o.draw(renderer, graphics2D, camera);
        }
    }

    public UUID getId() {
        return id;
    }

    public ArrayList<GObject> getObjects() {
        return objects;
    }

    public Vector3 getCentroid() {
        return centroid;
    }
}
