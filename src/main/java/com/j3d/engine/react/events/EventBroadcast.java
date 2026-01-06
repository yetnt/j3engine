package com.j3d.engine.react.events;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GObject;

import java.util.ArrayList;

/**
 * EventBroadcast is an abstract class which is used to represent the properties that the
 * called event may input and or return.
 */
public abstract class EventBroadcast<T> {
    /**
     * The event initiator
     */
    public final T emitter;

    /**
     * The Renderer instance.
     */
    public final Renderer renderer;

    /**
     * Default Constructor for EventBroadcast
     * @param e The initiator of the broadcast.
     * @param r The Renderer instance.
     */
    public EventBroadcast(T e, Renderer r) {
        emitter = e;
        renderer = r;
    }
}
