package com.j3d.engine.draw.tris;

import com.j3d.Static;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.util.UUID;

/**
 * TriListener listens for updates to a GTri and recalculates its distance from the camera.
 */
public class TriListener implements EventListener {

    public UUID triID;
    public GTri tri;
    public Vector3 lastPosition;
    public double lastDistanceFromCamera;
    public double depth;
    private boolean isDirty = false;

    public TriListener(GTri tri) {
        this.triID = tri.getId();
        this.lastPosition = tri.getPivot();
        this.tri = tri;
        this.lastDistanceFromCamera = tri.getPivot().sub(Static.camera.getPosition()).magnitude();
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
        Camera cam = Static.camera;
        GTri tri = (GTri) properties.emitter;

        this.lastPosition = tri.getPivot();
        this.lastDistanceFromCamera = tri.getPivot().sub(cam.getPosition()).magnitude();
        this.depth = tri.calcDepth();

        isDirty = true;
    }
}
