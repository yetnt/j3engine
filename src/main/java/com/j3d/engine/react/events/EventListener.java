package com.j3d.engine.react.events;

/**
 * EventListener is an interface which just allows Objects to listen for incoming Events at any time.
 * <p>
 *     An EventListener is an object that can be attached to an {@link EventEmitter} and will be called whenever the emitter broadcasts an event.
 * </p>
 */
public interface EventListener {
    /**
     * onEvent is called by {@link EventEmitter} via {@link EventEmitter#broadcast(EventType, EventPayload)}. Allowing the
     * emitter to call any other object that implements this.
     * @param event The type of event
     * @param properties The given event properties
     * @implNote This method is to be overridden by implementors.
     */
    public <K> void onEvent(EventType event, EventPayload<K> properties);
}
