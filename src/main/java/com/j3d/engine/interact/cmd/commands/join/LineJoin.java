package com.j3d.engine.interact.cmd.commands.join;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GCurve;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class LineJoin extends Subcommand {

    public LineJoin() {
        super("line", "Joins 2 points into a line an optionally a 3rd point into a curve");
        this.aliases("l").args(
                new TypedArg("point1", "The first point", false, GPoint.class),
                new TypedArg("point2", "The second point", false, GPoint.class),
                new TypedArg("point3", "The third point", true, GPoint.class)
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 2) {
            logLabel.setText("Invalid arguments " + aliasUsed + " <point> <point> <point?>");
            return;
        }
        if (!(args[0] instanceof GPoint p1 && args[1] instanceof GPoint p2)) {
            logLabel.setText("Invalid arguments " + aliasUsed + " <point> <point> <point?>");
            return;
        }
        GPoint third = null;
        if (args.length > 2) {
            if (!(args[2] instanceof GPoint p3)) {
                logLabel.setText("Invalid arguments " + aliasUsed + " <point> <point> <point?>");
                return;
            }
            third = p3;
        }

        Thing thing = StaticRefs.getSceneManager().findObjectParent(p1);

        boolean isCurve;
        if (third == null) {
            joinLine(thing, p1, p2);
            isCurve = false;
        }
        else {
            joinCurve(thing, p1, p2, third);
            isCurve = true;
        }

        logLabel.setText(
                "Joined " +
                        (isCurve ? "3 " : "2 ")
                + "points into new" +
                        (isCurve ? " curve" : " line")
                );
        logLabel.setLower(
                "(All objects have been moved to the first point's Thing with the name: "+ thing.getName()+")"
        );
    }

    public void joinLine(Thing t, GPoint p1, GPoint p2) {
        StaticRefs.getSceneManager().removeFromParent(p2, t);
        t.addObjs(
                p1, p2, new GLine(p1, p2)
        );
    }

    public void joinCurve(Thing t, GPoint p1, GPoint p2, GPoint p3) {
        StaticRefs.getSceneManager().removeFromParent(p2, t);
        StaticRefs.getSceneManager().removeFromParent(p3, t);
        t.addObjs(
                p1, p2, p3, new GCurve(p1, p3, p2)
        );
    }
}
