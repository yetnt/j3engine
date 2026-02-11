package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TypedArg;
import com.j3d.engine.react.actions.VoidAction;

import java.util.ArrayList;
import java.util.List;

class ScaleThing extends Subcommand {
    public ScaleThing() {
        super("scale", "Scales a Thing uniformly or along axes");
        this.args(
                new TypedArg("thing", "The thing to scale", false, Thing.class),
                new TypedArg("scale", "The scale factor (uniform or vector)", false, Double.class, Vector3.class)
        );

        this.usages.put(
                new ArrayList<>(List.of(Thing.class, Double.class)),
                "<Thing> (number)"
        );
        this.usages.put(
                new ArrayList<>(List.of(Thing.class, Vector3.class)),
                "<Thing> (vector3)"
        );
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        if (args.length != 2 || !(args[0] instanceof Thing t)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, Double.class)[0] + " or " + returnUsagesWhere(aliasUsed, Thing.class, Vector3.class)[0]);
            return;
        }

        if (args[1] instanceof Double s) {
            VoidAction action = t.scale(s);
            com.j3d.engine.Renderer.history.add(action);
            action.run();
            logLabel.setText("Thing scaled uniformly by " + s);
        } else if (args[1] instanceof Vector3 v) {
            VoidAction action = t.scale(v);
            Renderer.history.add(action);
            action.run();
            logLabel.setText("Thing scaled by vector " + v);
        } else {
            logLabel.setText("Invalid scale argument. Must be a number or vector3.");
        }
    }
}
