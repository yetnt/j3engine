package com.j3d.engine.react.events.payloads;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.guide.GuideFlow;
import com.j3d.gen.guide.GuideInfo;

import java.util.UUID;

/**
 * Event payload for when a {@link GuideInfo} receives the event it was listening to, and hence needs to request
 * a close of itself from the {@link GuideFlow}. This event is fired by {@link GuideFlow} receives this event
 * and decides whether {@link GuideFlow} can close.
 * <p>
 *     This stores nothing more than the UUID of the GuideInfo that is closing.
 * </p>
 * @see com.j3d.gen.guide
 * @see GuideInfo
 * @see GuideFlow
 * @see EventType#GUIDE_CLOSING
 * @author Lehlogonolo Poole
 */
public class GuideInfoClosingPayload extends EventPayload<GuideInfo> {

    private final UUID uuid;

    public GuideInfoClosingPayload(GuideInfo e) {
        super(e);
        this.uuid = e.getId();
    }

    public UUID getId() {
        return uuid;
    }
}
