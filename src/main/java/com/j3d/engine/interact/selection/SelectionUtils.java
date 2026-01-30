package com.j3d.engine.interact.selection;

/**
 * Standard utility functions to use for or in relation to selecting.
 */
public abstract class SelectionUtils {

    /**
     * Enumeration for selection type.
     */
    public enum InferredSelectionType {
        /**
         * No keys were pressed. Normal selection.
         */
        NONE,
        /**
         * VK_I was pressed. Subtract from existing selections
         */
        SUBTRACT,
        /**
         * VK_I and VK_SHIFT were pressed. Add to existing selections
         */
        ADD
    }

    /**
     * Returns the appropriate selection variant based on the inferred selection type and strictness.
     *
     * @param st The inferred selection type (NONE, SUBTRACT, ADD).
     * @param isStrict A boolean indicating if the selection is strict (true) or soft (false).
     * @param addCase The value to return if the inferred selection type is ADD.
     * @param subCase The value to return if the inferred selection type is SUBTRACT.
     * @param strictCase The value to return if the inferred selection type is NONE and the selection is strict.
     * @param softCase The value to return if the inferred selection type is NONE and the selection is soft.
     * @param <T> The type of the return value.
     * @return The selected variant.
     */
    public static <T> T usingSelectionVariant(InferredSelectionType st, boolean isStrict, T addCase, T subCase, T strictCase, T softCase) {
        return switch (st) {
            case ADD -> addCase;
            case SUBTRACT -> subCase;
            case NONE -> isStrict ? strictCase : softCase;
        };
    }
}
