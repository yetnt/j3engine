package com.j3d.engine.react.events.payloads;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;

// technically its overkill using an entire class for this
// but the objects already check for this anyway.

/**
 * Event Payload for when a GPoint moves and the parent geometry be it a {@link GLine}, {@link GTri}, etc,
 * needs say it's centroid to be recalculated to maintain consistency.
 * @see GPoint
 * @see EventType#GPOINT_RECALC_PIVOT
 * @author Lehlogonolo Poole
 */
public class GPointMovedEvent extends EventPayload<GPoint> {
    public GPointMovedEvent(GPoint e) {
        super(e);
    }
}
