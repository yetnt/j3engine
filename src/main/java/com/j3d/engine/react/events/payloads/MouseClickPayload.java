package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

import java.awt.event.MouseEvent;

/**
 * Event payload for when a mouse click occurs.
 * <p>
 *     This stores the {@link MouseEvent} that occurred.
 * </p>
 * @see EventType#MOUSE_CLICKED
 * @see AlwaysMouseOwner
 * @author Lehlogonolo Poole
 */
public class MouseClickPayload extends EventPayload<AlwaysMouseOwner> {
    private final MouseEvent event;

    public MouseClickPayload(AlwaysMouseOwner owner, MouseEvent event) {
        super(owner);
        this.event = event;
    }

    public MouseEvent getEvent() {
        return event;
    }

}
