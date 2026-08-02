package com.j3d.engine.draw;

import com.j3d.StaticRefs;
import com.j3d.engine.draw.methods.CamDepthSort;
import com.j3d.engine.draw.methods.CamDistSort;
import com.j3d.engine.draw.methods.DDUUIDSort;
import com.j3d.engine.draw.methods.VisibleSort;
import com.j3d.engine.geometry.geo2d.graphics.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Renderer is a static class that manages all GTri objects in the scene
 * and their rendering order based on their distance from the camera.
 * The main purpose of this class is to optimize the rendering process by
 * maintaining a sorted list of GTri objects, allowing for efficient rendering
 * based on whichever method is chosen as this class will have multiple methods for
 * sorting GTri in the future.
 * <p>
 *     The inner package "methods" contains the different sorting methods that Renderer can use.
 *     It's configured by the SceneManager (or debug settings) to choose which method to use for sorting GTri objects.
 *     This is mostly for testing and performance comparison purposes, as when a method is chosen,
 *     it will be used for all rendering cycles until changed.
 * </p>
 * @author Lehlogonolo Poole
 * @see PureListener
 * @see GTri
 * @see PureSortMethod
 * @see SortMethod
 * @see CamDepthSort
 * @see CamDistSort
 * @see DDUUIDSort
 * @see VisibleSort
 */
public class Renderer {
    /**
     * Holds all registered TriListeners. These are registered when a GTri is created
     * and unregistered when a GTri is deleted.
     */
    private static final ArrayList<PureListener> registered = new  ArrayList<>();
    /**
     * A list used for sorting GTri based on whichever method Renderer uses.
     * The list is cleared after each render cycle.
     * @implNote This more references a deque but for reusability and clarity
     * an ArrayList was used instead.
     * This queue is not also a pure ArrayList at runtime
     * but rather a subclass of {@link SortMethod}. Which is how the triangle
     * sorting is done.
     */
    private static ArrayList<Drawable> queue;

    static {
        // later set bucket sort to
        setSortMethod(PureSortMethod.CAMDISTSORT);
    }

    /**
     * Sets the sort method for Renderer.
     * @param method The PureSortMethod to set.
     */
    public static void setSortMethod(PureSortMethod method) {
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
     * Registers a GTri with Renderer.
     * @implSpec This method should only be called when a GTri is instantiated.
     * This is due to the fact that it will live its entirely lifetime within
     * Renderer. Therefore only {@link GTri#GTri(Color, GLine, GLine, GLine)}
     * or the other constructor {@link GTri#GTri(Color, GPoint, GPoint, GPoint)}
     * should ever have to call this method.
     * @param tri The GTri to register.
     */
    public static void register(Drawable tri) {
        PureListener listener = new PureListener(tri);
//        tri.attachListener(listener);
        registered.add(listener);
        queue.add(tri);
    }

    /**
     * Unregisters a triangle from Renderer
     * @param tri The triangle to unregister.
     * @implSpec Much like the docs within {@link Renderer#register(Drawable)}
     * however here only {@link GTri#deleteSelf()} may call this method or
     * any other destructive method.
     */
    public static void unregister(Drawable tri) {
        // finder listener that matches tri id
        registered.stream().filter(
                listener -> listener.triID.equals(tri.rendererUUID())
        ).findFirst().ifPresent(
                registered::remove
        );
    }

    /**
     * Clears the Renderer queue.
     */
    public static void clearQueue() {
        queue.clear();
    }

    /**
     * Draws all the triangles that have been sorted within the queue.
     * @param g The Graphics2D context.
     */
    public static void draw(Graphics2D g) {
        ArrayList<GObject> unparented = StaticRefs.getSceneManager().getUnparented().stream()
                .map(o -> (GObject) o)
                .collect(Collectors.toCollection(ArrayList::new));
        unparented.forEach(
                u -> {
                    // draw these fools first since we cant use Renderer methods for sorting.
                    // upper todo remove later. primitives.
                    if (StaticRefs.getSceneManager().getSelected().contains(u)) {
                        u.drawSelected(g);
                    } else {
                        u.draw(g);
                    }
                }
        );
        for  (Drawable drawable : queue) {
            if (drawable instanceof GTri tri)
                if (tri.isHidden()) continue;
            if (StaticRefs.getSceneManager().getSelected().contains(drawable.objectParent())) {
                drawable.drawSelected(g);
            } else {
                drawable.draw(g);
            }
        }
        clearQueue();
    }

    /**
     * Adds a GTri to the queue.
     * @param tri The GTri to add.
     */
    public static void addToQueue(GTri tri) {
        if (registered.stream().anyMatch(
                l -> l.tri == tri
        )) {
            queue.add(tri);
        }
    }

    /**
     * Clears all registered TriListeners.
     */
    public static void clearRegistered() {
        registered.clear();
    }

    public static int trisRegistered() {
        return registered.size();
    }
}
