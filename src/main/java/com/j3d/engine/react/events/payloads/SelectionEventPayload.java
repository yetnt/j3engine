package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.scene.nodes.geometry.GObject;

import java.util.HashSet;

/**
 *  Event payload for when objects are selected.
 * <p>
 *     This stores the {@link HashSet} of {@link GObject}s that have been selected.
 * </p>
 * @see EventType#X_SELECTED
 * @see SelectionMouseOwner
 * @see SelectionManager
 * @see GObject
 * @author Lehlogonolo Poole
 */
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
