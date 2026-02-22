package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;

import static com.j3d.engine.interact.cmd.commands.transform.TransformCmd.selectDispatch;

public class RotateSelection extends Subcommand {

    RotateSelection() {
        super("rotate", "Rotates the selection");
        this.aliases("rot", "r").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        selectDispatch(SC_Option.ROTATE);
    }
}
