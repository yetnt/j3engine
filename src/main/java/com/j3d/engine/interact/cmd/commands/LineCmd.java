package com.j3d.engine.interact.cmd.commands;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TypedArg;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LineCmd extends Command {
    public LineCmd() {
        super("line", "Creates a line in 3D space.");
        this.aliases("ln")
            .args(
                new TypedArg("start", "The starting position of the line or a point", false, Vector3.class, GPoint.class),
                new TypedArg("end", "The ending position of the line or a point", false, Vector3.class, GPoint.class)
            );

        // Custom Usage Args parsing because the args have to be of the same type.

        usages.put(
                new ArrayList<>(List.of(Vector3.class, Vector3.class)), " (vector3) (vector3)"
        );
        usages.put(
                new ArrayList<>(List.of(GPoint.class, GPoint.class)), " <point> <point>"
        );
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        if (args.length != 2) {
            logLabel.setText("Invalid number of arguments. Usage:" + returnUsagesWhere(aliasUsed, Vector3.class)[0] + " or " + returnUsagesWhere(aliasUsed, GPoint.class)[0]);
            return;
        }
        if (!(args[0] instanceof Vector3 || args[0] instanceof GPoint) ||
            !(args[1] instanceof Vector3 || args[1] instanceof GPoint)) {
            logLabel.setText("Invalid argument types. Usage:Usage:" + returnUsagesWhere(aliasUsed, Vector3.class)[0] + " or " + returnUsagesWhere(aliasUsed, GPoint.class)[0]);
            return;
        }
        // Arguments have to be of the same type
        if ((args[0] instanceof Vector3 && args[1] instanceof GPoint) ||
            (args[0] instanceof GPoint && args[1] instanceof Vector3)) {
            logLabel.setText("Arguments must be of the same type. Both Vector3 or both GPoint.");
            return;
        }

        GPoint startPos = null;
        GPoint endPos = null;

        // Determine start position
        if (args[0] instanceof Vector3 v1) {
            startPos = Static.renderer.findOrCreatePoint(v1, null);
        } else if (args[0] instanceof GPoint p1) {
            startPos = p1;
        } else {
            logLabel.setText("Invalid type for start argument. Must be Vector3 or GPoint.");
            return;
        }

        // Determine end position
        if (args[1] instanceof Vector3 v2) {
            endPos = Static.renderer.findOrCreatePoint(v2, null);
        } else if (args[1] instanceof GPoint p2) {
            endPos = p2;
        } else {
            logLabel.setText("Invalid type for end argument. Must be Vector3 or GPoint.");
            return;
        }

        // Create the line
        new Thing(Static.renderer, null, "Line").addObjs(
                new GLine(startPos, endPos), startPos, endPos);
        logLabel.setText("Line created from " + startPos.getPivot() + " to " + endPos.getPivot());
        J3DSettings.log.println("Line created from " + startPos.getPivot() + " to " + endPos.getPivot());
    }
}
