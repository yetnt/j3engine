package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.input.mouse.MOwner;

import java.awt.event.MouseEvent;

public class ScaleMouseOwner extends TransformMouseOwner {

    public Handle handle;

    public ScaleMouseOwner() {
        super(MOwner.SCALE_HANDLE);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
    }
}
