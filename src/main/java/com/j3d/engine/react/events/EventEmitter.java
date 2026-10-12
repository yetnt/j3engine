package com.j3d.engine.react.events;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Attaches a listener to the event emitter.
     * @param event The listener to attach
     */
    public void attachListener(@NotNull EventListener event) {
        genericAttach(registered, event);
    }


    /**
     * Detaches a listener from the event emitter.
     * @param event The listener to detach.
     */
    public void detachListener(@NotNull EventListener event) {
        genericDetach(registered, event);
    }


    /**
     * Detaches all listeners from the event emitter.
     */
    public void detachAll() {
        genericDetachAll(registered);
    }


    /**
     * Broadcasts an event to all registered listeners.
     * @param eventType The event type.
     * @param properties Properties to pass onto the listener.
     * @param <K> The object held by the {@link EventPayload}.
     */
    @Contract(pure=true)
    public <K> void broadcast(@NotNull EventType eventType, @NotNull EventPayload<K> properties) {
        genericBroadcast(registered, eventType, properties);
    }


    /**
     * Attaches a listener to the event emitter. Useful for implementors of {@link EventEmitterInterface}.
     * @param events The list of current listeners
     * @param event  The listener to attach
     */
    public static void genericAttach(@NotNull ArrayList<EventListener> events, @NotNull EventListener event) {
        events.add(event);
    }

    /**
     * Detaches a listener from the event emitter. Useful for implementors of {@link EventEmitterInterface}.
     * @param events The list of current listeners
     * @param event  The listener to detach
     */
    public static void genericDetach(@NotNull ArrayList<EventListener> events, @NotNull EventListener event) {
        events.remove(event);
    }

    /**
     * Detaches all listeners from the event emitter. Useful for implementors of {@link EventEmitterInterface}.
     * @param events The list of current listeners
     */
    public static void genericDetachAll(@NotNull ArrayList<EventListener> events) {
        events.clear();
    }

    /**
     * Broadcasts an event to all registered listeners. Useful for implementors of {@link EventEmitterInterface}.
     * @param events The list of current listeners
     * @param eventType The event type.
     * @param properties Properties to pass onto the listener.
     * @param <K> The object held by the {@link EventPayload}.
     */
    public static <K> void genericBroadcast(@NotNull ArrayList<EventListener> events, @NotNull EventType eventType, @NotNull EventPayload<K> properties) {
//        StaticRefs.getLog().println(
//                "[EVENTEMITTER] " + properties.emitter.getClass().getSimpleName() + " : " + eventType.toString() + " to " + events.size() + " listeners"
//        ); Infinite event listening bye-bye
        ArrayList<EventReactor> reactors = new ArrayList<>();
        new ArrayList<>(events).forEach(event -> {
            event.onEvent(eventType, properties);
            if (event instanceof EventReactor er)
                reactors.add(er);
        });
        reactors.forEach(events::remove);
        reactors.clear();
    }

    @Override
    public boolean isAttached(@NotNull EventListener e) {
        return registered.contains(e);
    }
}
