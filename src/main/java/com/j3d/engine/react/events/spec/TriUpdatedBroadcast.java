package com.j3d.engine.react.events.spec;

import com.j3d.engine.Renderer;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.geometry.geo2d.GTri;

/**
 * TriUpdatedBroadcast is an EventPayload that is used to represent the properties
 * when a GTri is updated.
 */
public class TriUpdatedBroadcast extends EventPayload<GTri> {
    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     * @param r The Renderer instance.
     */
    public TriUpdatedBroadcast(GTri e, Renderer r) {
        super(e, r);
    }
}
