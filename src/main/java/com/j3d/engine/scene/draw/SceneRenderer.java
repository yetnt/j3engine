package com.j3d.engine.scene.draw;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.methods.CamDepthSort;
import com.j3d.engine.scene.draw.methods.CamDistSort;
import com.j3d.engine.scene.draw.methods.DDUUIDSort;
import com.j3d.engine.scene.draw.methods.VisibleSort;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;

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
     * A list used for sorting GTri based on whichever method SceneRenderer uses.
     * The list is cleared after each render cycle.
     * @implNote This more references a deque but for reusability and clarity
     * an ArrayList was used instead.
     * This queue is not also a pure ArrayList at runtime
     * but rather a subclass of {@link SortMethod}. Which is how the triangle
     * sorting is done.
     */
    private SortMethod queue;

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
            case CAMDISTSORT, NONE -> new CamDistSort();
            case VISIBLESORT ->  new VisibleSort();
            case CAMDEPTHSORT -> new CamDepthSort();
            case DDUUIDSORT -> new DDUUIDSort();
            //case BUCKETSORT ->new BucketSort(registered);
            default -> new CamDistSort();
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
        new ArrayList<>(queue).stream().filter(
                listener -> listener.getId().equals(tri.getId())
        ).findFirst().ifPresent(
                queue::remove
        );
    }

    public void unregister(UUID uuid) {
        // finder listener that matches tri id
        new ArrayList<>(queue).stream()
                .filter(Objects::nonNull)
                .filter(
                listener -> listener.getId().equals(uuid)
        ).forEach(
                queue::remove
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
        queue.sort();
        for  (RenderState<?, ?> drawable : new ArrayList<>(queue)) {
            if (!drawable.isValid()) {
                unregister(drawable);
                unregister(drawable.getId());
                continue;
            }
            if (drawable.getPure() instanceof GTri tri)
                if (tri.isHidden()) continue;
            if (StaticRefs.getSceneManager().getSelected().contains(drawable.getParent())) {
                drawable.drawSelected(g);
            } else {
                drawable.draw(g);
            }
        }
        clearQueue();
    }

    public int pureRegistered() {
        return queue.size();
    }
}
