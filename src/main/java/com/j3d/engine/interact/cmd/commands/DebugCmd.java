package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;

import java.util.ArrayList;

public class DebugCmd extends Command {
    public DebugCmd() {
        super("debug", "Toggle debug mode");
        this.aliases("dbg", "test").args(
                new EchoCmd(),
                new TypeOf()
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
}
