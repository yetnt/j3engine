package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TypedArg;

public class LookAtCmd extends Command {
    public LookAtCmd() {
        super("lookAt", "Makes the camera look at an object");
        this.aliases("la", "look", "lookat").args(
                new TypedArg("thing", "the name of the thing to look at or otherwise coordinates.", false, String.class, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        if (args.length != 1 || (!(args[0] instanceof String) && !(args[0] instanceof Vector3))) {
            logLabel.setText("Invalid arguments. Usage: lookat <thing: String>");
            return;
        }

        if (args[0] instanceof String thing) {
            Thing t = Static.renderer.findThing(thing);
            if (t == null) {
                logLabel.setText("No thing found with the name " + thing);
                return;
            }

            Static.camera.lookAt(t.getCentroid());
        } else {
            Static.camera.lookAt((Vector3) args[0]);
        }
    }
}
