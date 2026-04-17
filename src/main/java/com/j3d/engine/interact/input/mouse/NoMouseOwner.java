package com.j3d.engine.interact.input.mouse;

/**
 * NoMouseOwner is a MouseOwner which represents the absence of a mouse owner in the sceneManager.
 * It is used as the default mouse owner in the EngineFrame when no other MouseOwner has requested ownership of the mouse input.
 */
public class NoMouseOwner extends MouseOwner {
    public NoMouseOwner() {
        super(MOwner.NONE);
    }
}
