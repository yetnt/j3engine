package com.j3d.engine.interact.cmd.avail;

import com.j3d.J3DSettings;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TypedArg;

import javax.swing.*;

public class DebugCmd extends Command {
    public DebugCmd() {
        super("debug", "Toggle debug mode");
        this.aliases("dbg", "test").args(
                new EchoCmd(),
                new TypeOf()
        ).parseUsages();
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: debug <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args);
    }

    public static class TypeOf extends Subcommand {
        public TypeOf() {
            super("typeof", "Returns the type of the input argument.");
                this.args(
                    new TypedArg("input", "The input to check the type of", false, Any.class)
                ).parseUsages();
        }
        @Override
        public void run(javax.swing.JLabel logLabel, String aliasUsed, Object... args) {
            if (args.length != 1) {
                logLabel.setText("Invalid arguments. Usage: typeof <input>");
                return;
            }
            String typeName = (args[0] == null) ? "null" : args[0].getClass().getName();
            logLabel.setText("Type: " + typeName);
            J3DSettings.log.println("Type: " + typeName);
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
        public void run(javax.swing.JLabel logLabel, String aliasUsed, Object... args) {
            if (args.length != 1 || !(args[0] instanceof String message)) {
                logLabel.setText("Invalid arguments. Usage: echo <message: String>");
                return;
            }
            logLabel.setText(message);
            J3DSettings.log.println(message);
        }
    }
}
