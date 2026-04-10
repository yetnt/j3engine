package com.j3d.engine.geometry.constraints;

import com.j3d.engine.geometry.geo2d.constraints.*;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;

import java.util.Set;

/**
 * Defines a rule that applies to a specific geometric object, ensuring its properties
 * adhere to certain conditions.
 * <p>
 *     An example would be if a point {@code A} is constrained to the midpoint of a line {@code B}.
 *     Point {@code A} would hold a {@code ConstraintOn<GPoint>} object. This constraint would
 *     then validate any proposed transformations (e.g., from a "translate" command) to ensure
 *     that {@code A} only moves if the line {@code B} moves in a way that preserves the
 *     midpoint relationship.
 * </p>
 * @param <P> The type of the object that this constraint is applied to (the "parent").
 * @author Lehlogonolo Poole
 * @see ConstraintMirror
 * @see ConstraintIntent
 * @see ConstraintUtils
 * @see CObject
 * @see CPoint
 * @see CLine
 * @see CTri
 */
public interface ConstraintOn<P> {
    /**
     * Returns the user-friendly name of the constraint.
     *
     * @return The name of the constraint (e.g., "Midpoint").
     */
    String name();

    /**
     * Provides a brief description of what the constraint does.
     *
     * @return A short description of the constraint's rule.
     */
    String description();

    /**
     * The parent object this constraint applies to.
     * @return The parent object
     */
    P getParent();

    /**
     * Validates a proposed transformation against this constraint's rule.
     * <p>
     * This method uses the "what-if" scenario encapsulated in the {@link ConstraintIntent}
     * to determine if the proposed change would violate the rule.
     *
     * @param intent The intent containing the proposed transformation and the mirrored objects.
     * @return {@code true} if the constraint is still satisfied after the transformation, {@code false} otherwise.
     */
    boolean satisfiesConstraint(ConstraintIntent intent);

    /**
     * Gets a {@link Runnable} that, when executed, actively enforces the constraint.
     * <p>
     * This is used to "snap" the parent object into a valid state according to the rule.
     * For example, it could move a point to the exact midpoint of a line.
     *
     * @return A {@link Runnable} that applies the constraint logic.
     */
    Runnable getConstraintApplier();

    /**
     * A convenience method that executes the constraint's applier.
     * This will force the parent object to conform to the constraint's rule.
     */
    default void applyConstraint() {
        getConstraintApplier().run();
    }

    /**
     * Returns a set of other constraints that are fundamentally incompatible with this one.
     * <p>
     * This is used by a {@link ConstraintManager} to prevent the user from applying conflicting
     * rules to the same object. For example, a "Fixed" constraint would be incompatible with a
     * "Midpoint" constraint on the same point.
     *
     * @return A {@link Set} of incompatible {@code ConstraintOn} instances.
     */
    Set<Class<?>> incompatibleWith();
}
