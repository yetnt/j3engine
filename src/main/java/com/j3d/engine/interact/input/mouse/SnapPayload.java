package com.j3d.engine.interact.input.mouse;

import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.react.events.EventPayload;

/**
 * Represents an event payload specifically for snapping operations, carrying information
 * about the {@link GObject} that is being snapped to.
 */
public class SnapPayload extends EventPayload<SnapMouseOwner> {
    private final GObject snap;
    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     * @param snap The object to snap to.
     */
    public SnapPayload(SnapMouseOwner e, GObject snap) {
        super(e);
        this.snap = snap;
    }

    /**
     * Retrieves the {@link GObject} that this payload indicates should be snapped to.
     *
     * @return The {@link GObject} target for the snap operation.
     */
    public GObject getSnap() {
        return snap;
    }
}
