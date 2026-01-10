package com.j3d.engine.interact.input.mouse;

public enum MOwner {
    /**
     * No mouse owner
     */
    NONE,
    /**
     * Owner for selection actions
     */
    SELECTION,
    /**
     * Owner for translation actions
     */
    TRANSLATE_HANDLE,
    /**
     * Owner for rotation actions
     */
    ROTATE_HANDLE,
    /**
     * Owner for scaling actions
     */
    SCALE_HANDLE,
}
