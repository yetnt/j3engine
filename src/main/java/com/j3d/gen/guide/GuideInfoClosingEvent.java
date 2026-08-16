package com.j3d.gen.guide;

import com.j3d.engine.react.events.EventPayload;

import java.util.UUID;

public class GuideInfoClosingEvent extends EventPayload<GuideInfo> {

    private final UUID uuid;

    public GuideInfoClosingEvent(GuideInfo e) {
        super(e);
        this.uuid = e.getId();
    }

    public UUID getId() {
        return uuid;
    }
}
