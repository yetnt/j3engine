package com.j3d.engine.react.events;

/**
 * A simple interface for implementors to use such that they dont bloat some specific setting
 * due to multiple events firing that do the same thing.
 * @param <T> The type of the event payload.
 * @param <V> The duplicate object to check against.
 * @implSpec It's up to the implementor to return if the calculated {@code V} value is identical
 * to the one it has stored. Therefore a convenience method has been made {@link #getDupeObjectToCheck()}
 * such that it's implemented.
 * @author Lehlogonolo Poole
 * @see EventListener
 */
public interface IdempotentEventListener<T extends EventPayload<?>, V> {
    /**
     * Returns the duplicate object that events of type {@code T} will calculate/generate.
     * @return The duplicate object to check against.
     */
    V getDupeObjectToCheck();

    /**
     * The handler to pass through for this specific type.
     * @param type The type of event.
     * @param payload The payload given.
     * @implSpec Implementors need to provide their own override which checks if the state
     * did not change, and if it didn't early exit. Making only 1 event make meaningful changes
     * and not duplicates.
     */
    void handlePossibleDuplicates(EventType type, T payload);
}
