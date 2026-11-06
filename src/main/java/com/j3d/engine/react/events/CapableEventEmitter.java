package com.j3d.engine.react.events;

import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * What makes this different from your generic {@link EventEmitter} is that, this has runtime support
 * for {@link CapableEventListener}, which returns a value. And further returns said value via {@link #capableBroadcast(EventType, EventPayload)}
 * @implSpec calling {@link #broadcast(EventType, EventPayload)} for an implementor of this isn't sufficient as
 * the actual capabilities are within {@link #capableBroadcast(EventType, EventPayload)}
 * @param <T> The type {@link #capableBroadcast(EventType, EventPayload)} will return and the one that is expected. in the sense
 *           that if the type returned by {@link CapableEventListener} is the one matched by this, then the method immediately returns.
 * @author Lehlogonolo Poole
 * @see CapableEventListener
 * @see EventEmitter
 */
public class CapableEventEmitter<T> extends EventEmitter {

    private final Class<T> typeToken;

    /**
     * Instantiates a new CapableEventEmitter bound to a specific runtime type.
     * @param typeToken The class literal representing type T.
     */
    public CapableEventEmitter(@NotNull Class<T> typeToken) {
        this.typeToken = typeToken;
    }

    /**
     * Broadcasts an event to all registered listeners
     * @implSpec This method returns early if {@link CapableEventListener} is registered and returns the correct
     * type. otherwise it functions identically to {@link EventEmitter#genericBroadcast(ArrayList, EventType, EventPayload)}
     * @param eventType The event type.
     * @param properties Properties to pass onto the listener.
     * @param <K> The object held by the {@link EventPayload}.
     * @return A pair containing the original listener and the return value by that listener, or null.
     */
    @Nullable
    @Contract(pure=true)
    public <K> Pair<CapableEventListener<T>, T> capableBroadcast(@NotNull EventType eventType, @NotNull EventPayload<K> properties) {
        ArrayList<EventReactor> reactors = new ArrayList<>();

        for (EventListener event : new ArrayList<>(registered)) {
            if (event instanceof CapableEventListener<?> capableEventListener) {
                Object result = capableEventListener.capableOnEvent(eventType, properties);
                if (typeToken.isInstance(result))
                    return new Pair<>((CapableEventListener<T>) capableEventListener, typeToken.cast(result));
            }
            event.onEvent(eventType, properties);
            if (event instanceof EventReactor er) reactors.add(er);
        }
        reactors.forEach(registered::remove);
        reactors.clear();
        return null;
    }
}

