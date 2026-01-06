package com.j3d.engine.react.events;

import java.util.ArrayList;

/**
 * EventEmitter is an abstract class that allows for other classes to attach
 * events that they can broadcast to at any time.
 */
public abstract class EventEmitter {
    /**
     * All registered EventListeners
     */
    protected ArrayList<EventListener> registered = new ArrayList<>();

    public EventEmitter() {
    }

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
     * Deregisters all event listeners.
     */
    public void detachAll() {
        registered.clear();
    }

    /**
     * Calls all events with the given event type and broadcast properties
     * @param eventType The event type.
     * @param properties Properties to pass onto the listener.
     */
    public <K> void broadcast(EventType eventType, EventBroadcast<K> properties) {
        registered.forEach(event -> event.onEvent(eventType, properties));
    }
}
