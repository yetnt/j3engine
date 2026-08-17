package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

/**
 * Event payload for when the centre of a transformation changes.
 * <p>
 *     This stores the {@link ScreenPoint} of where the user clicked to change the centre to.
 * </p>
 * @see EventType#TRANSFORM_CHANGE_CENTRE
 * @see TransformMouseOwner
 * @see ScreenPoint
 * @author Lehlogonolo Poole
 */
public class ChangeCentreEventPayload extends EventPayload<TransformMouseOwner> {
    private ScreenPoint mousePos;

    public ChangeCentreEventPayload(TransformMouseOwner e, ScreenPoint position) {
        super(e);
        this.mousePos = position;
    }

    public ScreenPoint getMousePos() {
        return mousePos;
    }
}
