package com.j3d.engine.geometry.constraints;

import com.j3d.Static;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.utility.SetDeque;

import java.util.stream.Stream;

/**
 * Manages a collection of constraints for a specific object or system.
 * <p>
 * This class is responsible for adding, removing, and retrieving constraints,
 * ensuring that no incompatible constraints are added. It uses a {@link SetDeque}
 * to store the constraints, guaranteeing uniqueness and providing ordered access.
 *
 * @param <T> The type of object that the constraints are applied to. This is typically
 *            a geometric object like a GPoint or GLine.
 */
public class ConstraintManager<T> {
    /**
     * The collection of constraints managed by this instance.
     */
    private final SetDeque<ConstraintOn<T>> constraints = new SetDeque<>();

    /**
     * Constructs a new, empty ConstraintManager.
     */
    public ConstraintManager() {}

    /**
     * Adds a new constraint to the manager.
     * <p>
     * Before adding, it checks if the new constraint is incompatible with any of the
     * existing constraints.
     *
     * @param constraint The constraint to add.
     * @throws IllegalArgumentException if the new constraint clashes with an existing one.
     */
    public void addConstraint(ConstraintOn<T> constraint) {
        if (constraintClashes(constraint)) {
            throw new IllegalArgumentException("Constraint clashes with existing constraints");
        }
        constraints.add(constraint);
    }

    /**
     * Removes a constraint from the manager.
     *
     * @param constraint The constraint to remove.
     */
    public void removeConstraint(ConstraintOn<T> constraint) {
        constraints.remove(constraint);
    }

    /**
     * Returns the underlying collection of constraints.
     *
     * @return The {@link SetDeque} containing all managed constraints.
     */
    public SetDeque<ConstraintOn<T>> getConstraints() {
        return constraints;
    }

    /**
     * Returns a new {@link Stream} of the constraints.
     * <p>
     * This method provides a safe way to iterate over the constraints without
     * risking concurrent modification issues, as it operates on a copy.
     *
     * @return A stream of the constraints.
     */
    public Stream<ConstraintOn<T>> constraintStream() {
        return constraints.copy().stream();
    }

    /**
     * Checks if a new constraint is incompatible with any of the currently managed constraints.
     * <p>
     * A clash occurs if any existing constraint lists the new constraint in its
     * {@link ConstraintOn#incompatibleWith()} list.
     *
     * @param newConstraint The new constraint to check.
     * @return {@code true} if a clash is found, {@code false} otherwise.
     */
    private boolean constraintClashes(ConstraintOn<T> newConstraint) {
        return constraints.stream()
                .flatMap(existingConstraint -> existingConstraint.incompatibleWith().stream())
                .anyMatch(incompatible -> incompatible.isInstance(newConstraint));
    }

    public boolean allSatisfied(String errStartText, ConstraintIntent intent) {
        for (ConstraintOn<T> constraint : constraints) {
            boolean accepted = constraint.satisfiesConstraint(intent);
            if (!accepted) {
                Static.hoverLabel.error(
                        constraint.getParent().getClass().getSimpleName()
                                + " Constraint | "
                                + errStartText, constraint.name());
                return false;
            }
        }
        return true;
    }
}
