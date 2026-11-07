package com.j3d.engine.interact.cmd.commands.join;

import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.scene.nodes.geometry.GPoint;

public class PolyLineJoin extends Subcommand {

    public PolyLineJoin() {
        super("line", "Joins 2 points into a line an optionally a 3rd point into a curve");
        this.aliases("l").args(
                new TypedArg("point1", "The first point", false, GPoint.class),
                new TypedArg("point2", "The second point", false, GPoint.class),
                new TypedArg("point3", "The third point", true, GPoint.class)
        ).parseUsages();
    }
}
