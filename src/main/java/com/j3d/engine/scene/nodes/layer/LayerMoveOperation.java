package com.j3d.engine.scene.nodes.layer;

public enum LayerMoveOperation {
    /**
     * Layer should move above the layer that precedes it.
     */
    FORWARD,
    /**
     * Layer should move below the layer that follows it.
     */
    BACKWARD,
    /**
     * Layer should move to the top of the layer list. (Position 1, 0 is the default)
     */
    TO_TOP,
    /**
     * Layer should move to the bottom of the layer list.
     */
    TO_BOTTOM
}
