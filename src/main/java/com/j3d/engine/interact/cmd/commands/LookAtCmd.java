package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;

import java.util.ArrayList;

public class LookAtCmd extends Command {
    public LookAtCmd() {
        super("lookAt", "Makes the camera look at an object");
        this.aliases("la", "look", "lookat").args(
                new TypedArg("thing", "the name or UUID of the thing to look at or otherwise coordinates.", false,
                        String.class, Vector3.class, Thing.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || (!(args[0] instanceof String) && !(args[0] instanceof Vector3))) {
            logLabel.setText("Invalid arguments. Usage: lookat <thing: String|Vector3|UUID>");
            return;
        }

        if (args[0] instanceof String thing) {
            Thing t = Static.sceneManager.findThing(thing);
            if (t == null) {
                logLabel.setText("No thing found with the name " + thing);
                return;
            }

            Static.camera.lookAt(t.getCentroid());
        } else if (args[0] instanceof Vector3) {
            Static.camera.lookAt((Vector3) args[0]);
        } else {
            Static.camera.lookAt(
                    Static.sceneManager.findThing((String) args[0]).getCentroid()
            );
        }
    }
}
