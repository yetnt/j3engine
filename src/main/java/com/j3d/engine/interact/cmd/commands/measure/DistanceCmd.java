package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class DistanceCmd extends Subcommand {
    public DistanceCmd() {
        super("distance", "Measure distance");
        this.aliases("d", "dist").args(
                new TypedArg("a", "point A", false, GPoint.class, Vector3.class),
                new TypedArg("b", "point B", false, GPoint.class, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 2) {
            logLabel.setText("No arg given?");
            return;
        }
        Vector3 A = switch (args[0]) {
            case GPoint p -> p.getPivot();
            case Vector3 v -> v;
            default ->  null;
        };
        Vector3 B = switch (args[1]) {
            case GPoint p -> p.getPivot();
            case Vector3 v -> v;
            default ->  null;
        };
        if (A == null || B == null) {
            logLabel.setText("One of the inputs was not given a type of vector3");
            return;
        }

        logLabel.setText(A.distance(B) + " units long");

    }
}
