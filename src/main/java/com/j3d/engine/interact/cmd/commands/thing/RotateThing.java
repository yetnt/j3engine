package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;
import com.j3d.engine.react.actions.VoidAction;

import java.util.ArrayList;

class RotateThing extends Subcommand {
    private final ArgSet axisSet = new ArgSet("axis", "The axis to rotate around", false, "x", "y", "z", "c");

    public RotateThing() {
        super("rot", "Rotates a Thing around a specified axis");
        this.args(
                new TypedArg("thing", "The thing to rotate", false, Thing.class),
                axisSet,
                new TypedArg("angle", "The angle in degrees", false, Double.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length != 3 || !(args[0] instanceof Thing t) || !(args[1] instanceof String axis) || !(args[2] instanceof Double v)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, String.class, Double.class)[0]);
            return;
        }
        axis = axis.toLowerCase();
        if (!axisSet.isValid(axis)) {
            logLabel.setText("Invalid axis. Must be 'x', 'y', 'z', or 'c'. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, String.class, Double.class)[0]);
            return;
        }
        switch (axis) {
            case "x" -> {
                // Vector representing X axis
                Vector3 xAxis = new Vector3(1, 0, 0);
                VoidAction action = t.rotate(xAxis, v);
                com.j3d.engine.Renderer.history.add(action);
            }
            case "y" -> {
                // Vector representing Y axis
                Vector3 yAxis = new Vector3(0, 1, 0);
                VoidAction action = t.rotate(yAxis, v);
                com.j3d.engine.Renderer.history.add(action);
            }
            case "z" -> {
                // Vector representing Z axis
                Vector3 zAxis = new Vector3(0, 0, 1);
                VoidAction action = t.rotate(zAxis, v);
                com.j3d.engine.Renderer.history.add(action);
            }
            case "c" -> {
                // Rotate around centroid
                if (t.getCentroid() == null) {
                    logLabel.setText("Thing has no points to determine centroid for 'c' axis rotation.");
                    return;
                }
                VoidAction action = t.rotate(t.getCentroid().normalize(), v);
                Renderer.history.add(action);
            }
            default -> {
                logLabel.setText("Invalid axis. Must be 'x', 'y', 'z', or 'c'. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, String.class, Double.class)[0]);
                return;
            }
        }
        logLabel.setText("Thing rotated around " + axis + "-axis by " + v);
    }
}
