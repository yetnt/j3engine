package com.j3d.engine.geometry.constraints;

import com.j3d.engine.geometry.geo2d.constraints.*;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Defines the contract for a "mirror" object used in the constraint validation system.
 * <p>
 * A ConstraintMirror is a lightweight, temporary, data-only copy of a "real" geometric
 * object (like a GPoint or GLine). Its purpose is to act as a safe stand-in or
 * "sandbox" where potential transformations can be applied and tested without affecting
 * the original object.
 * <p>
 * This interface is the foundation of the transactional, "what-if" nature of the
 * constraint system.
 *
 * @author Lehlogonolo Poole
 * @see ConstraintOn
 * @see ConstraintManager
 * @see ConstraintIntent
 * @see CPoint
 * @see CLine
 * @see CTri
 * @see CObject
 */
public interface ConstraintMirror {
    /**
     * Checks if this mirror's state has been modified from its original, mirrored state.
     * <p>
     * A mirror becomes "stale" after {@link #dispose()} has been called.
     * This flag can be used to identify which mirrors within an intent have actually been changed.
     * </p>
     * @implSpec
     *     {@link #dispose()} is only called after {@link ConstraintIntent#consume()} has been applied.
     *     This means, within the {@link Consumer} all mirrors aren't (supposed to be) stale.
     *
     * @return {@code true} if the mirror's data has been altered or disposed, {@code false} if it
     *         still perfectly mirrors the original object's state.
     */
    boolean isStale();

    /**
     * Returns the unique identifier of the original object that this mirror represents.
     * <p>
     * This ID is crucial for linking the temporary mirror back to the "real"
     * geometric object in the scene.
     *
     * @return The {@link UUID} of the original object.
     */
    UUID getId();

    /**
     * Invalidates and disposes of this mirror, marking it as stale.
     * <p>
     * This method is called when the mirror is no longer needed, typically after a
     * constraint validation process is complete.
     * @implSpec Implementors should use this method
     * to perform any necessary clean-up and to set the internal state that {@link #isStale()}
     * checks to {@code true}. This prevents the mirror from being accidentally reused.
     */
    void dispose();
}
