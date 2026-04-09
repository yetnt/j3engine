package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;

import java.awt.*;
import java.util.ArrayList;

public class TriCmd extends Command {
    public TriCmd() {
        super("triangle", "Creates a triangle in 3D space.");
        this.aliases("tri", "tr")
            .args(
                 new TypedArg("v1", "The position of the first vertex or line", false, Vector3.class, GPoint.class, GLine.class),
                 new TypedArg("v2", "The position of the second vertex or line", false, Vector3.class, GPoint.class, GLine.class),
                 new TypedArg("v3", "The position of the third vertex or line", false, Vector3.class, GPoint.class, GLine.class),
                    new TypedArg("col", "The color of the triangle", true, Color.class)
            );

        // Custom Usage Args parsing because the args have to be of the same type.

        usages.put(
                new java.util.ArrayList<>(java.util.List.of(Vector3.class, Vector3.class, Vector3.class, Color.class)), "(vector3) (vector3) (vector3) #color?# ...key:value"
        );
        usages.put(
                new java.util.ArrayList<>(java.util.List.of(GPoint.class, GPoint.class, GPoint.class, Color.class)), "<point> <point> <point> #color?# ...key:value"
        );
        usages.put(
                new java.util.ArrayList<>(java.util.List.of(GLine.class, GLine.class, GLine.class, Color.class)), "<line> <line> <line> #color?# ...key:value"
        );
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length != 3 && args.length != 4) {
            logLabel.setText("Invalid number of arguments. Usage:" + returnUsagesWhere(aliasUsed, Vector3.class)[0] + " or " + returnUsagesWhere(aliasUsed, GPoint.class)[0] + " or " + returnUsagesWhere(aliasUsed, GLine.class)[0]);
            return;
        }
        // Each argument has to be either a Vector3, GPoint, or GLine

        for (Object arg : args) {
            if (!(arg instanceof Vector3 || arg instanceof GPoint || arg instanceof GLine || arg instanceof Color)) {
                logLabel.setText("Invalid argument types. Usage:" + returnUsagesWhere(aliasUsed, Vector3.class)[0] + " or " + returnUsagesWhere(aliasUsed, GPoint.class)[0] + " or " + returnUsagesWhere(aliasUsed, GLine.class)[0]);
                return;
            }
        }
        // Arguments have to be of the same type
        boolean allVector3 = args[0] instanceof Vector3 && args[1] instanceof Vector3 && args[2] instanceof Vector3;
        boolean allGPoint = args[0] instanceof GPoint && args[1] instanceof GPoint && args[2] instanceof GPoint;
        boolean allGLine = args[0] instanceof GLine && args[1] instanceof GLine && args[2] instanceof GLine;

        if (!allVector3 && !allGPoint && !allGLine) {
            logLabel.setText("Arguments must be of the same type. All Vector3, all GPoint, or all GLine.");
            return;
        }

        if (args.length == 4 && !(args[3] instanceof Color)) {
            logLabel.setText("Invalid type for color argument.");
            return;
        }

        Color col = (args.length == 4) ? (Color) args[3] : Color.WHITE;

        if (allGPoint || allVector3) {
            GPoint[] vertices = new GPoint[3];
            for (int i = 0; i < 3; i++) {
                if (args[i] instanceof Vector3 v) {
                    vertices[i] = new GPoint(v);
                } else if (args[i] instanceof GPoint p) {
                    vertices[i] = p;
                }
            }
            new Thing(Static.renderer, null, "Triangle").addObjs(new GTri(col, vertices[0], vertices[1], vertices[2]), vertices[0], vertices[1], vertices[2]);
            logLabel.setText("Triangle created with vertices: " + vertices[0] + ", " + vertices[1] + ", " + vertices[2]);
        } else {
            GLine[] lines = new GLine[3];
            for (int i = 0; i < 3; i++) {
                lines[i] = (GLine) args[i];
            }
            try {
                new Thing(Static.renderer, null, "Triangle").addObjs(new GTri(col, lines[0], lines[1], lines[2]), lines[0], lines[1], lines[2]);
                logLabel.setText("Triangle created with lines: " + lines[0] + ", " + lines[1] + ", " + lines[2]);
            } catch (IllegalArgumentException e) {
                logLabel.setText("Error creating triangle: " + e.getMessage());
            }
        }
    }
}
