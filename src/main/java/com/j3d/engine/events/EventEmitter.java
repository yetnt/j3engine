package com.j3d.engine.events;

import java.util.ArrayList;

/**
 * EventEmitter is an abstract class that allows for other classes to attach
 * events that they can broadcast to at any time.
 */
public abstract class EventEmitter {
    /**
     * All registered EventListeners
     */
    ArrayList<EventListener> registered = new ArrayList<>();

    /**
     * Registers an event listener into the list of listeners.
     * @param event The listener to attach
     */
    public void attach(EventListener event) {
        registered.add(event);
    }

    /**
     * Deregisters an event listener.
     * @param event The listener to detach.
     */
    public void detach(EventListener event) {
        registered.remove(event);
    }

    /**
     * Calls all events with the given event type and broadcast properties
     * @param type The event type.
     * @param properties Properties to pass onto the listener.
     */
    public void broadcast(EventType type, EventBroadcast properties) {
        registered.forEach(event -> event.onEvent(type, properties));
    }
}
