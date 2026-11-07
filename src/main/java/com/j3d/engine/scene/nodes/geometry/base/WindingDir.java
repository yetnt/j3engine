package com.j3d.engine.scene.nodes.geometry.base;

// NOTE: CW = FRONT, CCW = BACK
public enum WindingDir {
    CLOCKWISE, COUNTERCLOCKWISE;

    public boolean asBoolean() {
        return switch (this) {
            case CLOCKWISE -> true;
            case COUNTERCLOCKWISE -> false;
        };
    }

    public static WindingDir fromBoolean(boolean bool) {
        return bool ? CLOCKWISE : COUNTERCLOCKWISE;
    }
}
