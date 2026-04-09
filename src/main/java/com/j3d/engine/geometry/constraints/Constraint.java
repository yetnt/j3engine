package com.j3d.engine.geometry.constraints;

import com.j3d.engine.geometry.geo2d.constraints.*;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;

/**
 * A Constraint is a specific rule that applies to any graphics object.
 * Specifically {@link GObject}s and soon {@link Thing}.
 * <p>
 *     An example would be. if {@code A} is a point defined to be constrained to line {@code B}'s midpoint.
 *     Point {@code A} will hold a {@link Constraint} object which applies the constraint and safe guards
 *     against commands. As in, a command like {@code translate} cannot apply that transform to {@code A}
 *     unless the entire line moves in unison then it will.
 * </p>
 * @param <P> The type of the instance that holds the constraint.
 * @author Lehlogonolo Poole
 * @see ConstraintMirror
 * @see ConstraintIntent
 * @see ConstraintUtils
 * @see CObject
 * @see CPoint
 * @see CLine
 * @see CTri
 */
public interface Constraint<P> {
    /**
     * The parent object this constraint applies to.
     * @return The parent object
     */
    P getParent();

    /**
     * Method to check whether some operation will still satisfy the constraint.
     * @param intent The intent to check
     * @return true if the constraint is preserved with this intent. False otherwise.
     */
    boolean satisfiesConstraint(ConstraintIntent intent);

    /**
     * Gets the runnable that applies the constraint.
     * @return The runnable
     */
    Runnable getConstraintApplier();

    /**
     * Applies the constraint.
     */
    default void applyConstraint() {
        getConstraintApplier().run();
    };
}
