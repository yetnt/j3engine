package com.j3d.engine.interact.selection;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.scene.nodes.geometry.GObject;

import java.util.HashSet;

public class SelectionEventPayload extends EventPayload<SelectionMouseOwner> {

    private final HashSet<GObject> selectedSet;

    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     */
    public SelectionEventPayload(SelectionMouseOwner e, HashSet<GObject> selected) {
        super(e);
        selectedSet = new HashSet<>(selected);
    }

    public HashSet<GObject> getSelectedSet() {
        return selectedSet;
    }
}
