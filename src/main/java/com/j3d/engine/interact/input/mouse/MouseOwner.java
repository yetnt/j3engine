package com.j3d.engine.interact.input.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class MouseOwner {
    private final MouseAdapter mouseAdapter;
    private final MouseMotionAdapter mouseMotionAdapter;

    public MouseOwner(MouseAdapter mouseAdapter, MouseMotionAdapter mouseMotionAdapter) {
        this.mouseAdapter = mouseAdapter;
        this.mouseMotionAdapter = mouseMotionAdapter;
    }

    public MouseAdapter getMouseAdapter() {
        return mouseAdapter;
    }

    public MouseMotionAdapter getMouseMotionAdapter() {
        return mouseMotionAdapter;
    }
}
