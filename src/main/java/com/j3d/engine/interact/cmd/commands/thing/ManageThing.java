package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TypedArg;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class ManageThing extends Subcommand {
    private ArgSet actionSet = new ArgSet("action", "The action to perform", false, "add", "remove");

    public ManageThing() {
        super("obj", "Adds or removes objects to/from a Thing");
        this.args(
                actionSet,
                new TypedArg("thing", "The thing to manage", false, Thing.class),
                new TypedArg("obj", "The object to add/remove", false, GTri.class, GLine.class, GPoint.class)
        );

        // Manually define usage since obj arg can be multiple types

        this.usages.put(
                new ArrayList<>(List.of(String.class, Thing.class, GTri.class)),
                "[add|remove] <Thing> <triangle>"
        );
        this.usages.put(
                new ArrayList<>(List.of(String.class, Thing.class, GLine.class)),
                "[add|remove] <Thing> <line>"
        );
        this.usages.put(
                new ArrayList<>(List.of(String.class, Thing.class, GPoint.class)),
                "[add|remove] <Thing> <point>"
        );
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        if (args.length != 3 || !(args[0] instanceof String action) || !(args[1] instanceof Thing t) ||
                !(args[2] instanceof GTri || args[2] instanceof GLine || args[2] instanceof GPoint)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, String.class, Thing.class, GTri.class, GLine.class, GPoint.class)[0]);
            return;
        }
        action = action.toLowerCase();
        if (!actionSet.isValid(action)) {
            logLabel.setText("Invalid action. Must be 'add' or 'remove'. Usage:" + returnUsagesWhere(aliasUsed, String.class, Thing.class, GTri.class, GLine.class, GPoint.class)[0]);
            return;
        }
        if (action.equals("add")) {
            GObject obj = (GObject) args[2];
            Thing oldParent = Static.renderer.findObjectParent(obj);
            if (oldParent != null) {
                oldParent.getObjects().remove(obj);
                t.addObjs(obj);
                logLabel.setText("Object moved from Thing " + oldParent.getId() + " to Thing " + t.getId());
            }
        } else if (action.equals("remove")) {
            GObject obj = (GObject) args[2];
            if (t.getObjects().contains(obj)) {
                t.getObjects().remove(obj);
                logLabel.setText("Object removed from Thing");
            } else {
                logLabel.setText("Thing does not contain the specified object");
            }
        }

    }
}
