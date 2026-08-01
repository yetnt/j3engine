package com.j3d.engine.react.events.spec;

import com.j3d.engine.SceneManager;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

/**
 * TriUpdatedBroadcast is an EventPayload that is used to represent the properties
 * when a GTri is updated.
 */
public class TriUpdatedBroadcast extends EventPayload<GTri> {
    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     * @param r The SceneManager instance.
     */
    public TriUpdatedBroadcast(GTri e, SceneManager r) {
        super(e);
    }
}
