package com.j3d.engine.interact.input;

import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;

import java.awt.event.MouseEvent;

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
