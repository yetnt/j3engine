package com.j3d.engine.draw;

import com.j3d.StaticRefs;
import com.j3d.engine.draw.methods.CamDepthSort;
import com.j3d.engine.draw.methods.CamDistSort;
import com.j3d.engine.draw.methods.DDUUIDSort;
import com.j3d.engine.draw.methods.VisibleSort;
import com.j3d.engine.geometry.geo2d.graphics.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * SceneRenderer is a static class that manages all GTri objects in the scene
 * and their rendering order based on their distance from the camera.
 * The main purpose of this class is to optimize the rendering process by
 * maintaining a sorted list of GTri objects, allowing for efficient rendering
 * based on whichever method is chosen as this class will have multiple methods for
 * sorting GTri in the future.
 * <p>
 *     The inner package "methods" contains the different sorting methods that SceneRenderer can use.
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
public class SceneRenderer {
    /**
     * Holds all registered TriListeners. These are registered when a GTri is created
     * and unregistered when a GTri is deleted.
     */
    private final ArrayList<PureListener> registered = new  ArrayList<>();
    /**
     * A list used for sorting GTri based on whichever method SceneRenderer uses.
     * The list is cleared after each render cycle.
     * @implNote This more references a deque but for reusability and clarity
     * an ArrayList was used instead.
     * This queue is not also a pure ArrayList at runtime
     * but rather a subclass of {@link SortMethod}. Which is how the triangle
     * sorting is done.
     */
    private ArrayList<RenderState<?, ?>> queue;

    public SceneRenderer() {
        // later set bucket sort to
        setSortMethod(PureSortMethod.CAMDISTSORT);
    }
    /**
     * Sets the sort method for SceneRenderer.
     * @param method The PureSortMethod to set.
     */
    public void setSortMethod(PureSortMethod method) {
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
     * Registers a GTri with SceneRenderer.
     * @implSpec This method should only be called when a GTri is instantiated.
     * This is due to the fact that it will live its entirely lifetime within
     * SceneRenderer. Therefore only {@link GTri#GTri(Color, GLine, GLine, GLine)}
     * or the other constructor {@link GTri#GTri(Color, GPoint, GPoint, GPoint)}
     * should ever have to call this method.
     * @param tri The GTri to register.
     */
    public void register(RenderState<?, ?> tri) {
        PureListener listener = new PureListener(tri);
//        tri.attachListener(listener);
        registered.add(listener);
        queue.add(tri);
    }

    /**
     * Unregisters a triangle from SceneRenderer
     * @param tri The triangle to unregister.
     * @implSpec Much like the docs within {@link SceneRenderer#register(RenderState)}
     * however here only {@link GTri#deleteSelf()} may call this method or
     * any other destructive method.
     */
    public void unregister(RenderState<?, ?> tri) {
        // finder listener that matches tri id
        new ArrayList<>(registered).stream().filter(
                listener -> listener.triID.equals(tri.getId())
        ).findFirst().ifPresent(
                registered::remove
        );
    }

    public void unregister(UUID uuid) {
        // finder listener that matches tri id
        new ArrayList<>(registered).stream()
                .filter(Objects::nonNull)
                .filter(
                listener -> listener.triID.equals(uuid)
        ).forEach(
                registered::remove
        );
    }

    /**
     * Clears the SceneRenderer queue.
     */
    public void clearQueue() {
        queue.clear();
    }

    /**
     * Draws all the triangles that have been sorted within the queue.
     * @param g The Graphics2D context.
     */
    public void draw(Graphics2D g) {
        for  (RenderState<?, ?> drawable : queue) {
            if (!drawable.isValid()) {
                unregister(drawable);
                unregister(drawable.getId());
            }
            if (drawable.getPure() instanceof GTri tri)
                if (tri.isHidden()) continue;
            // todo remove drawig parent,
            if (drawable.getPure() instanceof GCurve gc) {
                if (StaticRefs.getSceneManager().getSelected().contains(drawable.getParent())) {
                    drawable.drawSelected(g);
                } else
                    drawable.draw(g);
            }
            if (StaticRefs.getSceneManager().getSelected().contains(drawable.getParent())) {
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
    public void addToQueue(RenderState<?, ?> tri) {
        if (registered.stream().anyMatch(
                l -> l.tri == tri
        )) {
            queue.add(tri);
        }
    }

    /**
     * Clears all registered TriListeners.
     */
    public void clearRegistered() {
        registered.clear();
    }

    public int trisRegistered() {
        return registered.size();
    }
}
