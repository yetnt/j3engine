package com.j3d.engine.events.spec;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.geometry.geo2d.GTri;

/**
 * TriUpdatedBroadcast is an EventBroadcast that is used to represent the properties
 * when a GTri is updated.
 */
public class TriUpdatedBroadcast extends EventBroadcast<GTri> {
    /**
     * Default Constructor for EventBroadcast
     *
     * @param e The initiator of the broadcast.
     * @param r The Renderer instance.
     */
    public TriUpdatedBroadcast(GTri e, Renderer r) {
        super(e, r);
    }
}
