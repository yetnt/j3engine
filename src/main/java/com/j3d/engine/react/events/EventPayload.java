package com.j3d.engine.react.events;

import org.jetbrains.annotations.NotNull;

/**
 * EventPayload is an abstract class which is used to represent the properties that the
 * called event may input and or return.
 * @param <T> The type of the emitter, used for ease of access to the emitter's properties and methods.
 */
public abstract class EventPayload<T> {

    protected final T emitter;

    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     */
    public EventPayload(@NotNull T e) {
        emitter = e;
    }

    /**
     * The event initiator
     */
    public T getEmitter() {
        return emitter;
    }
}
