package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.base.*;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.utility.ClipboardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebugCmd extends Command {
    public DebugCmd() {
        super("debug", "Toggle debug mode");
        this.aliases("dbg", "test").args(
                new EchoCmd(),
                new TypeOf(),
                new RandomUUIDCmd(),
                new CameraInfoCmd()
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: debug <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }

    public static class TypeOf extends Subcommand {
        public TypeOf() {
            super("typeof", "Returns the type of the input argument.");
                this.args(
                    new TypedArg("input", "The input to check the type of", false, Any.class)
                ).parseUsages();
        }
        @Override
        public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
            if (args.length != 1 && taggedArgs.isEmpty()) {
                logLabel.setText("Invalid arguments. Usage: typeof <input>");
                return;
            }
            Object input = args.length == 1 ? args[0] : taggedArgs.getFirst();
            String typeName = input.getClass().getSimpleName();
            if (input instanceof TaggedArgValue<?> g)
                typeName = typeName + "<" + g.type.getSimpleName() + ">";

            logLabel.setText("Type: " + typeName);
            Static.log.println("Type: " + typeName);
        }
    }

    public static class EchoCmd extends Subcommand {
        public EchoCmd() {
            super("echo", "Echoes the input string.");
            this.args(
                    new TypedArg("message", "The message to echo", false, String.class)
                ).parseUsages();
        }
        @Override
        public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
            if (args.length != 1 || !(args[0] instanceof String message)) {
                logLabel.setText("Invalid arguments. Usage: echo <message: String>");
                return;
            }
            logLabel.setText(message);
            Static.log.println(message);
        }
    }

    public static class RandomUUIDCmd extends Subcommand {
        public RandomUUIDCmd() {
            super("id", "returns a random object uuid");
            parseUsages();
        }
        @Override
        public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
            List<GObject> objects = Static.renderer.layers
                    .stream()
                    .flatMap(Layer::stream)
                    .flatMap(Thing::objectsStream)
                    .toList();

            GObject random = objects.get(new Random().nextInt(objects.size()));
            logLabel.setText(random.getId().toString());
            ClipboardUtil.copyToClipboard(random.getId().toString());
        }
    }

    public static class CameraInfoCmd extends Subcommand {
        public ArgSet argSet =
                new ArgSet("type", "The type of info to return", true, "pos", "rot");
        public CameraInfoCmd() {
            super("camera", "Prints camera information to the console.");
            aliases("cam", "c").args(
                    argSet
            ).parseUsages();
        }

        @Override
        public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
            if (args.length > 0 && !(args[0] instanceof String)) {
                logLabel.setText("Invalid arguments. Usage: debug "+aliasUsed+" "+argSet.toUseString());
                return;
            }

            String rot = "rot("+
                    Static.camera.getRotation().getPitch() + ", "
                    + Static.camera.getRotation().getYaw() + ", "
                    + Static.camera.getRotation().getRoll() + ")";
            String pos = "pos("+
                    Static.camera.getPosition().getX() + ", "
                    + Static.camera.getPosition().getY() + ", "
                    + Static.camera.getPosition().getZ() + ")";

            String content = args.length == 0 ?
                    pos + " " + rot : args[0].equals("pos") ?
                    pos : rot;

            logLabel.setText(content);
            ClipboardUtil.copyToClipboard(content);
            Static.log.println(content);
        }

    }
}
