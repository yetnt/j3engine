package com.j3d.engine.interact;

/**
 * SelectionType is an enum that describes the different types of selections that can be made.
 */
public enum SelectionType {
    /**
     * Everything has been selected.
     */
    ALL,
    /**
     * Selection where the selected object has to be within the selection's boundaries.
     */
    BOUNDS_STRICT,
    /**
     * Selection where the selected object can be partially within the selection's boundaries.
     */
    BOUNDS_SOFT,
    /**
     * Inverts the selection. Such as if objects A and B exist, and you select A. Inverting the selection will
     * deselect A and select B.
     */
    INVERT,
    /**
     * When a selection already exists, This new selection will add its selection to the existing selection.
     */
    INCLUDE,
    /**
     * When a selection already exists, This new selection will remove selected from the existing selection.
     */
    EXCLUDE
}
