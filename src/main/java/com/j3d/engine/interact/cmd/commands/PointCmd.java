package com.j3d.engine.interact.cmd.commands;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TypedArg;

public class PointCmd extends Command {
    public PointCmd() {
        super("point", "Creates a point in 3D space.");
        this.aliases("pnt", "pt")
            .args(
                new TypedArg("v", "The position of the new point", false, Vector3.class)
            ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        if (args.length != 1 || !(args[0] instanceof Vector3 position)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Vector3.class)[0]);
            return;
        }

        new Thing(Static.renderer, null, "Point").addObjs(new GPoint(position));
        logLabel.setText("Point created at position: " + position);
        J3DSettings.log.println("Point created at position: " + position);
    }
}
