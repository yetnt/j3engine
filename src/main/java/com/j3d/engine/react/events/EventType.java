package com.j3d.engine.react.events;

/**
 * Events is an enum that describes the possible Events that a listener can listen for.
 */
public enum EventType {
    /**
     * The object was translated.
     */
    OBJ_UPDATED,
    /**
     * The object was deleted.
     */
    OBJ_DELETED,
    /**
     * The object was selected.
     */
    X_SELECTED,
    SETTINGS_CODE_UPDATED,
    SNAP_TO_OBJ, TRANSFORM_CHANGE_CENTRE, GPOINT_RECALC_PIVOT
}
