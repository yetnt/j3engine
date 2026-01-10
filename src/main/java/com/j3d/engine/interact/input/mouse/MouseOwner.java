package com.j3d.engine.interact.input.mouse;

import com.j3d.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseOwner extends MouseAdapter {
    private final MOwner owner;

    public MouseOwner(MOwner owner) {
        this.owner = owner;
    }

    public void requestOwnership() {
        Main.setMouseOwner(owner);
    }

    protected boolean isNotOwner() {
        return Main.getMouseOwner() != owner;
    }
}
