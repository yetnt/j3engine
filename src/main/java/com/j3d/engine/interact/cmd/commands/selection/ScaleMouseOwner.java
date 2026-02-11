package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;

public class ScaleMouseOwner extends MouseOwner {
    public ScaleMouseOwner() {
        super(MOwner.SCALE_HANDLE);
    }

}
