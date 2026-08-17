package com.j3d.engine.react.events;

import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.react.events.payloads.*;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.interact.input.mouse.SnapMouseOwner;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.gen.guide.GuideInfo;
import com.j3d.gen.settings.Setting;

/**
 * Events is an enum that describes the possible Events that a listener can listen for.
 */
public enum EventType {
    /**
     * The object was selected. Broadcast by {@link SelectionMouseOwner} with a payload class of
     * {@link SelectionEventPayload}
     */
    X_SELECTED,
    /**
     * A Setting was updated via code specifically and needs to be broadcast to the UI
     * so that it can react and change accordingly. Broadcast by any {@link Setting}
     * with a payload class of {@link SettingUpdatedPayload}
     */
    SETTING_CODE_UPDATED,
    /**
     * A Setting was updated. Broadcast by any {@link Setting} with no speciifc payload class.
     */
    SUPDATED,
    /**
     * A mouse owner has snapping enabled and the user double-right clicked to snap to some object.
     * Broadcast by {@link SnapMouseOwner} with a payload class of {@link SnapPayload}
     */
    SNAP_TO_OBJ,
    /**
     * The pivot of the transform command(s) has changed. Fired by {@link TransformMouseOwner}
     * with a payload class of {@link ChangeCentreEventPayload}
     */
    TRANSFORM_CHANGE_CENTRE,
    /**
     * Self-explanatory. Fired by a {@link GPoint} with a payload class of {@link GPointMovedEvent}
     */
    GPOINT_RECALC_PIVOT,
    /**
     * A command was fired. This event doesn't guarantee the command ran successfully. Broadcast
     * by {@link Command} with a payload class of {@link CommandFiredPayload}
     */
    COMMAND_FIRED,
    /**
     * Specifically na extender of {@link StatefulCommand} has released its statefulness either by
     * the user confirming the action or cancelling. Broadcast by {@link StatefulCommand}
     * with a payload class of {@link StatefulCommandCompletedPayload}
     */
    STATEFUL_COMMAND_COMPLETED,
    /**
     * A guide step is closing. Broadcast by {@link GuideInfo} with a payload class of
     * {@link GuideInfoClosingPayload}
     */
    GUIDE_CLOSING,
    /**
     * For anything that's too lazy to use mouse owners and rather just use events to listen
     * for mouse clicks. Broadcast by {@link AlwaysMouseOwner} with a
     * payload class of {@link MouseClickPayload}
     */
    MOUSE_CLICKED,
}
