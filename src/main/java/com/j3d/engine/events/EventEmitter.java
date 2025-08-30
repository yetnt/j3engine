package com.j3d.engine.events;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * EventEmitter is an abstract class that allows for other classes to attach
 * events that they can broadcast to at any time.
 */
public abstract class EventEmitter {
    /**
     * All registered EventListeners
     */
    HashMap<ObjectType, ArrayList<EventListener>> registered = new HashMap<>();

    public EventEmitter() {
        registered.put(ObjectType.NODE, new ArrayList<>());
        registered.put(ObjectType.PARENT, new ArrayList<>());
    }

    /**
     * Registers an event listener into the list of listeners.
     * @param event The listener to attach
     * @param type The type of object
     */
    public void attach(EventListener event, ObjectType type) {
        switch (type) {
            case PARENT -> registered.get(ObjectType.PARENT).add(event);
            case NODE -> registered.get(ObjectType.NODE).add(event);
        }
    }

    /**
     * Deregisters an event listener.
     * @param event The listener to detach.
     * @param type The type of object
     */
    public void detach(EventListener event, ObjectType type) {
        switch (type) {
            case PARENT -> registered.get(ObjectType.PARENT).remove(event);
            case NODE -> registered.get(ObjectType.NODE).remove(event);
        }
    }

    /**
     * Calls all events with the given event type and broadcast properties
     * @param eventType The event type.
     * @param type The type of object
     * @param properties Properties to pass onto the listener.
     */
    public void broadcast(EventType eventType, ObjectType type, EventBroadcast properties) {
        switch (type) {
            case PARENT -> registered.get(ObjectType.PARENT).forEach(event -> event.onEvent(eventType, properties));
            case NODE -> registered.get(ObjectType.NODE).forEach(event -> event.onEvent(eventType, properties));
        }
    }
}
