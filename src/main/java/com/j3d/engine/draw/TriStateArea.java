package com.j3d.engine.draw;

import com.j3d.Static;
import com.j3d.engine.draw.methods.CamDepthSort;
import com.j3d.engine.draw.methods.CamDistSort;
import com.j3d.engine.draw.methods.DDUUIDSort;
import com.j3d.engine.draw.methods.VisibleSort;
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
    private static ArrayList<GTri> queue;

    static {
        // later set bucket sort to
        setSortMethod(TriangleSortMethod.CAMDISTSORT);
    }

    public static void setSortMethod(TriangleSortMethod method) {
        queue = switch (method) {
            case NONE -> new ArrayList<>();
            case CAMDISTSORT -> new CamDistSort(registered);
            case VISIBLESORT ->  new VisibleSort(registered);
            case CAMDEPTHSORT -> new CamDepthSort(registered);
            case DDUUIDSORT -> new DDUUIDSort(registered);
            //case BUCKETSORT ->new BucketSort(registered);
            default -> new CamDistSort(registered);
        };
    }

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
        for  (GTri tri : queue) {
            if (tri.isHidden()) continue;
            if (Static.renderer.getSelected().contains(tri)) {
                tri.drawSelected(g);
            } else {
                tri.draw(g);
            }
        }
    }

    public static void addToQueue(GTri tri) {
        queue.add(tri);
    }
}
