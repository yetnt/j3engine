package com.j3d.engine.interact.cmd.avail;

import com.j3d.Main;
import com.j3d.engine.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TypedArg;
import com.j3d.engine.react.actions.VoidAction;

import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.List;

public class ThingCmd extends Command {
    public ThingCmd() {
        super("thing", "A generic thing command");
        this.aliases("th", "ob").args(
                new NewThing(),
                new TranslateThing(),
                new ManageThing(),
                new RotateThing(),
                new ScaleThing()
        ).parseUsages();
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: thing <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args);
    }

    private static class NewThing extends Subcommand {

        public NewThing() {
            super("new", "Creates a new Thing");
            this.args(
                    new TypedArg("layerId", "The layer ID where the new thing will be added", true, String.class)
            ).parseUsages();
        }

        @Override
        public void run(JLabel logLabel, String aliasUsed, Object... args) {
            if (args.length > 1 || (args.length == 1 && !(args[0] instanceof String))) {
                logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, String.class)[0]);
                return;
            }
            Layer l = null;
            if (args.length == 1) {
                String layerId = (String) args[0];
                l = Main.renderer.findLayer(layerId);
            }
            new Thing(Main.renderer, l);
            logLabel.setText("New Thing created" + (l != null ? " in layer " + l.getIdentifier() : " in the default layer"));
        }
    }

    private static class TranslateThing extends Subcommand {

        public TranslateThing() {
            super("trans", "Translates a Thing");
            this.args(
                    new TypedArg("thing", "The thing to translate", false, Thing.class),
                    new TypedArg("vector3", "The translation vector", false, Vector3.class)
            ).parseUsages();
        }

        @Override
        public void run(JLabel logLabel, String aliasUsed, Object... args) {
            if (args.length != 2 || !(args[0] instanceof Thing t) || !(args[1] instanceof Vector3 v)) {
                logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, Vector3.class)[0]);
                return;
            }
            t.translate(v);
            logLabel.setText("Thing translated by " + v);
        }
    }

    private static class ManageThing extends Subcommand {
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
                Thing oldParent = Main.renderer.findObjectParent(obj);
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

    private static class RotateThing extends Subcommand {
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
        public void run(JLabel logLabel, String aliasUsed, Object... args) {
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
                    Renderer.history.add(action);
                }
                case "y" -> {
                    // Vector representing Y axis
                    Vector3 yAxis = new Vector3(0, 1, 0);
                    VoidAction action = t.rotate(yAxis, v);
                    Renderer.history.add(action);
                }
                case "z" -> {
                    // Vector representing Z axis
                    Vector3 zAxis = new Vector3(0, 0, 1);
                    VoidAction action = t.rotate(zAxis, v);
                    Renderer.history.add(action);
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

    private static class ScaleThing extends Subcommand {
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
        public void run(JLabel logLabel, String aliasUsed, Object... args) {
            if (args.length != 2 || !(args[0] instanceof Thing t)) {
                logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, Double.class)[0] + " or " + returnUsagesWhere(aliasUsed, Thing.class, Vector3.class)[0]);
                return;
            }

            if (args[1] instanceof Double s) {
                VoidAction action = t.scale(s);
                Renderer.history.add(action);
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

}
