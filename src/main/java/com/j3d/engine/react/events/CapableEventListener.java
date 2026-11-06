package com.j3d.engine.react.events;

/**
 * A specialization of an {@link EventListener} that is capable of returning a value
 * when handling an event.
 * <p>
 *     Unlike a generic {@link EventListener} whose {@link EventListener#onEvent(EventType, EventPayload)}
 *     method has a {@code void} return type, a {@code CapableEventListener} computes and returns
 *     a result of type {@code T} via {@link #capableOnEvent(EventType, EventPayload)}.
 * </p>
 * <p>
 *     This return value is specifically listened for and consumed by a {@link CapableEventEmitter}
 *     using {@link CapableEventEmitter#capableBroadcast(EventType, EventPayload)}. If the returned value
 *     matches the runtime type expected by the emitter's {@code typeToken}, the broadcast returns early
 *     with both this listener and the returned result.
 * </p>
 *
 * @param <T> The type of value returned by {@link #capableOnEvent(EventType, EventPayload)} and expected
 *            by the consuming {@link CapableEventEmitter}.
 * @author Lehlogonolo Poole
 * @see CapableEventEmitter
 * @see CapableEventEmitter#capableBroadcast(EventType, EventPayload)
 * @see EventListener
 * @see EventType
 * @see EventPayload
 */
public interface CapableEventListener<T> extends EventListener {

    /**
     * Handles an incoming event and computes a return value of type {@code T}.
     * <p>
     *     Invoked by {@link CapableEventEmitter#capableBroadcast(EventType, EventPayload)}. If the returned
     *     object is an instance of the emitter's bound type, the emitter terminates its broadcast early
     *     and returns this result.
     * </p>
     *
     * @param <K> The type of the object held by the {@link EventPayload}.
     * @param event The type of event being broadcast.
     * @param properties The payload containing properties and data for the event.
     * @return A value of type {@code T} produced in response to the event, or {@code null} if no meaningful
     *         value could be generated.
     * @implNote This method is meant to be overridden by implementors to provide event-handling
     *           logic with query-response capabilities.
     */
    <K> T capableOnEvent(EventType event, EventPayload<K> properties);
}
