package com.j3d.ui;

/**
 * An enum representing the names of custom cursors used in the app.
 */
public enum CursorNames {
    /**
     * The default cursor. This one may change
     */
    DEFAULT("default"),
    /**
     * Generic pointer cursor.
     */
    POINTER("default"),
    /**
     * Pointer cursor with a yellow question mark symbol
     */
    SELECT_SOFT("selectSoft"),
    /**
     * Pointer cursor with a green quesiton mark symbol
     */
    SELECT_STRICT("selectStrict"),
    /**
     * Pointer cursor with a red subtract symbol
     */
    SELECT_SUBTRACT("selectSubtract"),
    /**
     * Pointer cursor with a blue add symbol
     */
    SELECT_ADD("selectAdd"),
    /**
     * Hand cursor (Splayed open hand to represent dragging)
     */
    HAND_GRAB("grab"),
    /**
     * Hand cursor (Fist/Closed hand to represent dragging)
     */
    HAND_GRABBING("grabbing"),
    /**
     * Hand cursor (Index finger pointing at target)
     */
    HAND_POINTER("pointer");

    private String value;
    private CursorNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
