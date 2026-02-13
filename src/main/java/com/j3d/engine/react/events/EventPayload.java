package com.j3d.engine.react.events;

import com.j3d.engine.Renderer;

/**
 * EventPayload is an abstract class which is used to represent the properties that the
 * called event may input and or return.
 * @param <T> The type of the emitter, used for ease of access to the emitter's properties and methods.
 */
public abstract class EventPayload<T> {
    /**
     * The event initiator
     */
    public final T emitter;

    /**
     * The Renderer instance.
     */
    public final Renderer renderer;

    /**
     * Default Constructor for EventPayload
     * @param e The initiator of the broadcast.
     * @param r The Renderer instance.
     */
    public EventPayload(T e, Renderer r) {
        emitter = e;
        renderer = r;
    }
}
