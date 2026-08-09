package com.j3d.engine.interact.input.mouse;

import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseEvent;

/**
 * NoMouseOwner is a MouseOwner which represents the absence of a mouse owner in the sceneManager. It is
 * used as the default mouse owner in the EngineFrame when no other MouseOwner has requested ownership of
 * the mouse input.
 * @see EngineFrame
 */
public class NoMouseOwner extends MouseOwner {
    public NoMouseOwner() {
        super(MOwner.NONE);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (isNotOwner()) return;
        wrap(e);
    }
}
