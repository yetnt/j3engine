package com.j3d.engine.interact.input.mouse;

/**
 * MOwner is an enum which represents the different types of mouse owners in the sceneManager. It is used to determine which
 * MouseOwner is the owner of the mouse input in the sceneManager and to allow for easy switching between different MouseOwners.
 */
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
    /**
     * Owner for orbit the camera
     */
    ORBIT,
}
