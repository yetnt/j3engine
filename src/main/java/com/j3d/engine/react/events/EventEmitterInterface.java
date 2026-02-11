package com.j3d.engine.react.events;

/**
 * EventEmitterInterface is an interface that allows for other classes to attach
 * events that they can broadcast to at any time. This is available for classes that cannot extend {@link EventEmitter}
 * `but still want to have event broadcasting capabilities.
 */
public interface EventEmitterInterface {
    /**
     * Registers an event listener into the list of listeners.
     * @param event The listener to attach
     */
    void attach(EventListener event);
    /**
     * Deregisters an event listener.
     * @param event The listener to detach.
     */
    void detach(EventListener event);
    /**
     * Deregisters all event listeners.
     */
    void detachAll();
    /**
     * Calls all events with the given event type and broadcast properties
     * @param eventType The event type.
     * @param properties Properties to pass onto the listener.
     */
    <K> void broadcast(EventType eventType, EventBroadcast<K> properties);
}
