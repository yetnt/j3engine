package com.j3d.gen.guide.generic;

import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.MouseClickPayload;
import com.j3d.gen.guide.GuideInfo;

import java.awt.event.MouseEvent;

public class DoubleClickStep extends GuideInfo {

    public DoubleClickStep() {
        super(AlwaysMouseOwner.getSingleInstance());
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.MOUSE_CLICKED) {
            MouseClickPayload payload
                    = (MouseClickPayload) properties;
            MouseEvent e = payload.getEvent();
            if (e.getClickCount() != 2) return;

            close();
        }
    }
}
