package com.j3d.engine.react.events;

import java.util.ArrayList;

/**
 * EventEmitter is a class that allows for other classes to attach events that they can broadcast to at any time.
 * Any class that can extend {@link EventEmitter} should,
 * otherwise they can implement {@link EventEmitterInterface} if they cannot extend EventEmitter.
 */
public abstract class EventEmitter implements EventEmitterInterface {
    /**
     * All registered EventListeners
     */
    protected ArrayList<EventListener> registered = new ArrayList<>();

    /**
     * Creates a new EventEmitter with no registered listeners.
     */
    public EventEmitter() {
    }

    public void attach(EventListener event) {
        registered.add(event);
    }

    public void detach(EventListener event) {
        registered.remove(event);
    }

    public void detachAll() {
        registered.clear();
    }

    public <K> void broadcast(EventType eventType, EventBroadcast<K> properties) {
        ArrayList<EventReactor> reactors = new ArrayList<>();
        registered.forEach(event -> {
            event.onEvent(eventType, properties);
            if (event instanceof EventReactor er) {
                reactors.add(er);
            }
        });
        reactors.forEach(this::detach);
        reactors.clear();
    }
}
