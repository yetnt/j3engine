package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;

import java.util.ArrayList;

public class TeleportCmd extends Command {
    public TeleportCmd() {
        super("teleport", "Teleports the camera to the given coordinates");
        this.aliases("tp", "goto", "gt").args(
                new TypedArg("location", "the coordinates.", false, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || !(args[0] instanceof Vector3 v3)) {
            logLabel.setText("Invalid arguments. Usage: teleport <location: (Vector3)>");
            return;
        }

        Static.camera.setPosition(v3);
    }
}
