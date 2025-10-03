package com.j3d.engine.events;

/**
 * EventListener is an interface which just allows Objects to listen for incoming Events at any time
 */
public interface EventListener {
    /**
     * onEvent is called by {@link EventEmitter} via {@link EventEmitter#broadcast(EventType, EventBroadcast)}. Allowing the
     * emitter to call any other object that implements this.
     * @param event The type of event
     * @param properties The given event properties
     * @implNote This method is to be overridden by implementors.
     */
    public void onEvent(EventType event, EventBroadcast properties);
}
