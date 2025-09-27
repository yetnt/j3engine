package com.j3d.engine.events;

import com.j3d.engine.Renderer;

import java.awt.*;

/**
 * EventBroadcast is an abstract class which is used to represent the properties that the
 * called event may input and or return.
 */
public abstract class EventBroadcast {
    /**
     * The event initiator
     */
    public final EventEmitter emitter;

    /**
     * The Renderer instance.
     */
    public final Renderer renderer;

    /**
     * Default Constructor for EventBroadcast
     * @param e The initiator of the broadcast.
     * @param r The Renderer instance.
     */
    public EventBroadcast(EventEmitter e, Renderer r) {
        emitter = e;
        renderer = r;
    }
}
