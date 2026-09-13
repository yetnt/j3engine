package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.utility.generic.tuple.SamePair;

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
    private final SamePair<ScreenPoint> screenPoints;
    private final SelectionUtils.InferredSelectionType inferredSelectionType;

    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     */
    public SelectionEventPayload(SelectionMouseOwner e, HashSet<GObject> selected, ScreenPoint[] screenPnts, SelectionUtils.InferredSelectionType inferredSelection) {
        super(e);
        selectedSet = new HashSet<>(selected);
        screenPoints = new SamePair<>(screenPnts[0], screenPnts[1]);
        inferredSelectionType = inferredSelection;
    }

    public HashSet<GObject> getSelectedSet() {
        return selectedSet;
    }

    public SamePair<ScreenPoint> getScreenPoints() {
        return screenPoints;
    }

    public SelectionUtils.InferredSelectionType getInferredSelectionType() {
        return inferredSelectionType;
    }
}
