package com.j3d.engine.draw;

import com.j3d.Main;
import com.j3d.engine.draw.methods.CamDistSort;
import com.j3d.engine.geometry.geo2d.GTri;

import java.awt.*;
import java.util.ArrayList;

/**
 * TriStateArea is a static class that manages all GTri objects in the scene
 * and their rendering order based on their distance from the camera.
 * The main purpose of this class is to optimize the rendering process by
 * maintaining a sorted list of GTri objects, allowing for efficient rendering
 * based on whichever method is chosen as this class will have multiple methods for
 * sorting GTri in the future.
 * <p>
 *     The inner package "methods" contains the different sorting methods that TriStateArea can use.
 *     It's configured by the Renderer (or debug settings) to choose which method to use for sorting GTri objects.
 *     This is mostly for testing and performance comparison purposes, as when a method is chosen,
 *     it will be used for all rendering cycles until changed.
 * </p>
 */
public class TriStateArea {
    /**
     * Holds all registered TriListeners. These are registered when a GTri is created
     * and unregistered when a GTri is deleted.
     */
    private static final ArrayList<TriListener> registered = new  ArrayList<>();
    /**
     * A deque used for sorting GTri based on whichever method TriStateArea uses.
     * The deque is cleared after each render cycle.
     */
    private static final CamDistSort queue = new CamDistSort(registered);

    public static void register(GTri tri) {
        TriListener listener = new TriListener(tri);
        tri.attach(listener);
        registered.add(listener);
        queue.add(tri);
    }

    public static void unregister(GTri tri) {
        // find listener that matches tri id
        registered.stream().filter(
                listener -> listener.triID.equals(tri.getId())
        ).findFirst().ifPresent(
                registered::remove
        );
    }

    public static void draw(Graphics2D g) {
        for  (GTri tri : queue.reversed())
            if (Main.renderer.getSelected().contains(tri)) {
                tri.drawSelected(g);
            } else {
                tri.draw(g);
            }
    }

    public static void addToQueue(GTri tri) {
        queue.add(tri);
    }
}
