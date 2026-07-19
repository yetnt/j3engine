package com.j3d.engine.draw.tris;

import com.j3d.StaticRefs;
import com.j3d.engine.draw.tris.methods.CamDepthSort;
import com.j3d.engine.draw.tris.methods.CamDistSort;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.util.UUID;

/**
 * TriListener is an event listener who listens for any relevant updates
 * given to it to recalculate GTri properties. This listener is part of
 * {@link SortMethod} and is how an implementor of SortMethod should
 * lazily reload triangles.
 * <p>
 *     While this is given to all implementors, the current code is only
 *     utilised by {@link CamDistSort} and {@link CamDepthSort}
 * </p>
 * @author Lehlogonolo Poole
 * @see SortMethod
 * @see EventListener
 * @see GTri
 * @see EventType#OBJ_UPDATED
 */
public class TriListener implements EventListener {

    /**
     * The UUID of the triangle. Although this is the same UUID stored
     * within the {@link GTri} reference itself.
     */
    public UUID triID;
    /**
     * GTri reference.
     */
    public GTri tri;
    /**
     * The last position (centre) it was at.
     */
    public Vector3 lastPosition;
    /**
     * The last distance from the camera.
     */
    public double lastDistanceFromCamera;
    /**
     * The depth of the triangle.
     */
    public double depth;
    /**
     * The dirty state of the triangle. if it updated it's marked as dirty.
     */
    private boolean isDirty = false;

    public TriListener(GTri tri) {
        this.triID = tri.getId();
        this.lastPosition = tri.getPivot();
        this.tri = tri;
        this.lastDistanceFromCamera = tri.getPivot().sub(StaticRefs.camera.getPosition()).magnitude();
        this.depth = tri.calcDepth();
    }

    /**
     * Checks if the tri's state is dirty (i.e., has been updated).
     * If it is dirty, resets the dirty flag and returns true.
     * Otherwise, returns false.
     *
     * @return true if the tri is dirty, false otherwise.
     */
    public boolean isDirty() {
        if (isDirty) {
            isDirty = false;
            return true;
        }
        return false;
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event != EventType.OBJ_UPDATED)
            return;

        // When a tri is updated, recalculate its distance from the camera to ensure proper rendering order.
        Camera cam = StaticRefs.camera;
        GTri tri = (GTri) properties.emitter;

        this.lastPosition = tri.getPivot();
        this.lastDistanceFromCamera = tri.getPivot().sub(cam.getPosition()).magnitude();
        this.depth = tri.calcDepth();

        isDirty = true;
    }
}
