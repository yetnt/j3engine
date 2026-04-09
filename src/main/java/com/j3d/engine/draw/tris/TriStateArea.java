package com.j3d.engine.draw.tris;

import com.j3d.Static;
import com.j3d.engine.draw.tris.methods.CamDepthSort;
import com.j3d.engine.draw.tris.methods.CamDistSort;
import com.j3d.engine.draw.tris.methods.DDUUIDSort;
import com.j3d.engine.draw.tris.methods.VisibleSort;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

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
 * @author Lehlogonolo Poole
 * @see TriListener
 * @see GTri
 * @see TriangleSortMethod
 * @see SortMethod
 * @see CamDepthSort
 * @see CamDistSort
 * @see DDUUIDSort
 * @see VisibleSort
 */
public class TriStateArea {
    /**
     * Holds all registered TriListeners. These are registered when a GTri is created
     * and unregistered when a GTri is deleted.
     */
    private static final ArrayList<TriListener> registered = new  ArrayList<>();
    /**
     * A list used for sorting GTri based on whichever method TriStateArea uses.
     * The list is cleared after each render cycle.
     * @implNote This more references a deque but for reusability and clarity
     * an ArrayList was used instead.
     * This queue is not also a pure ArrayList at runtime
     * but rather a subclass of {@link SortMethod}. Which is how the triangle
     * sorting is done.
     */
    private static ArrayList<GTri> queue;

    static {
        // later set bucket sort to
        setSortMethod(TriangleSortMethod.CAMDISTSORT);
    }

    /**
     * Sets the sort method for TriStateArea.
     * @param method The TriangleSortMethod to set.
     */
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

    /**
     * Registers a GTri with TriStateArea.
     * @implSpec This method should only be called when a GTri is instantiated.
     * This is due to the fact that it will live its entirely lifetime within
     * TriStateArea. Therefore only {@link GTri#GTri(Color, GLine, GLine, GLine)}
     * or the other constructor {@link GTri#GTri(Color, GPoint, GPoint, GPoint)}
     * should ever have to call this method.
     * @param tri The GTri to register.
     */
    public static void register(GTri tri) {
        TriListener listener = new TriListener(tri);
        tri.attach(listener);
        registered.add(listener);
        queue.add(tri);
    }

    /**
     * Unregisters a triangle from TriStateArea
     * @param tri The triangle to unregister.
     * @implSpec Much like the docs within {@link TriStateArea#register(GTri)}
     * however here only {@link GTri#deleteSelf()} may call this method or
     * any other destructive method.
     */
    public static void unregister(GTri tri) {
        // find listener that matches tri id
        registered.stream().filter(
                listener -> listener.triID.equals(tri.getId())
        ).findFirst().ifPresent(
                registered::remove
        );
    }

    /**
     * Clears the TriStateArea queue.
     */
    public static void clearQueue() {
        queue.clear();
    }

    /**
     * Draws all the triangles that have been sorted within the queue.
     * @param g The Graphics2D context.
     */
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

    /**
     * Adds a GTri to the queue.
     * @param tri The GTri to add.
     */
    public static void addToQueue(GTri tri) {
        queue.add(tri);
    }

    /**
     * Clears all registered TriListeners.
     */
    public static void clearRegistered() {
        registered.clear();
    }
}
