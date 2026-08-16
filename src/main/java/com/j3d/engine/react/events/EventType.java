package com.j3d.engine.react.events;

import com.j3d.engine.interact.cmd.Commands;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.input.MouseClickPayload;
import com.j3d.engine.interact.input.SnapPayload;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.interact.input.mouse.SnapMouseOwner;
import com.j3d.gen.guide.GuideInfo;
import com.j3d.gen.guide.GuideInfoClosingEvent;

/**
 * Events is an enum that describes the possible Events that a listener can listen for.
 */
public enum EventType {
    /**
     * The object was translated.
     */
    OBJ_UPDATED,
    /**
     * The object was selected.
     */
    X_SELECTED,
    /**
     * Something updated the settings code itself and not the UI. tell the UI.
     */
    SETTINGS_CODE_UPDATED,
    /**
     * A mouse owner has snapping enabled and the user double-right clicked to snap to some object.
     * Broadcast by {@link SnapMouseOwner} with a payload class of {@link SnapPayload}
     */
    SNAP_TO_OBJ,
    /**
     * The pivot of the transform command(s) has changed.
     */
    TRANSFORM_CHANGE_CENTRE,
    /**
     * Self-explanatory
     */
    GPOINT_RECALC_PIVOT,
    /**
     * A command was fired. This event doesn't guarantee the command ran successfully. Broadcast
     * by {@link Command} with a payload class of {@link Commands.CommandFiredPayload}
     */
    COMMAND_FIRED,
    /**
     * A guide step is closing. Broadcast by {@link GuideInfo} with a payload class of
     * {@link GuideInfoClosingEvent}
     */
    GUIDE_CLOSING,
    /**
     * For anything that's too lazy to use mouse owners and rather just use events to listen
     * for mouse clicks. Broadcast by {@link AlwaysMouseOwner} with a
     * payload class of {@link MouseClickPayload}
     */
    MOUSE_CLICKED,
}
