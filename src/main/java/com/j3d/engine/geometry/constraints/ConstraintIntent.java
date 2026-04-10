package com.j3d.engine.geometry.constraints;

import org.hsqldb.Constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a proposed change to a set of geometric objects that needs to be
 * validated against constraints before being committed.
 * <p>
 * This class encapsulates a "what-if" scenario. It holds a collection of
 * {@link ConstraintMirror} objects (the "sandbox") and a {@link Consumer} (the "update")
 * that knows how to apply the proposed transformation to this sandbox.
 * <p>
 * The typical workflow is:
 * <ol>
 *     <li>A user action creates an intent with the proposed change.</li>
 *     <li>A {@link Constraint} receives this intent.</li>
 *     <li>The constraint calls {@link #consume()} to apply the change to the mirrors.</li>
 *     <li>The constraint then inspects the modified mirrors via {@link #map()} to check if the rule still holds.</li>
 * </ol>
 * @author Lehlogonolo Poole
 * @see ConstraintOn
 * @see ConstraintMirror
 */
public class ConstraintIntent {
    /**
     * A map of the mirrored objects involved in this intent, keyed by the UUID
     * of their original geometric objects.
     */
    private final HashMap<UUID, ConstraintMirror> affectedMirrors = new HashMap<>();

    /**
     * The function that applies the proposed transformation to the mirrored objects.
     */
    private final Consumer<HashMap<UUID, ConstraintMirror>> update;

    /**
     * Constructs a new ConstraintIntent.
     *
     * @param affected A list of {@link ConstraintMirror} objects that will be part of the "what-if" scenario.
     * @param update   A {@link Consumer} that contains the logic to apply the proposed transformation
     *                 to the mirrors held in this intent.
     */
    public ConstraintIntent(ArrayList<ConstraintMirror> affected, Consumer<HashMap<UUID, ConstraintMirror>> update) {
        this.update = update;
        affected.forEach(mirror -> affectedMirrors.put(mirror.getId(), mirror));
    }

    /**
     * Executes the proposed transformation on the mirrored objects held within this intent.
     * This is the "what-if" step, modifying the state of the sandbox for validation.
     */
    public void consume() {
        update.accept(affectedMirrors);
    }

    /**
     * Retrieves the map of mirrored objects involved in this intent.
     * <p>
     * This should be called after {@link #consume()} to access the modified state
     * of the mirrors for validation.
     *
     * @return The map of UUIDs to their corresponding {@link ConstraintMirror} objects.
     */
    public HashMap<UUID, ConstraintMirror> map() {
        return affectedMirrors;
    }
}
