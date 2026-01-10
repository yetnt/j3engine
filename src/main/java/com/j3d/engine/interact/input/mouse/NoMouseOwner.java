package com.j3d.engine.interact.input.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class NoMouseOwner extends MouseOwner {
    public NoMouseOwner() {
        super(MOwner.NONE);
    }
}
