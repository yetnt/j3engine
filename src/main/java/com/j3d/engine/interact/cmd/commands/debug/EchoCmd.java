package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.util.SafeJLabel;

import java.util.ArrayList;

/**
 * A subcommand of {@link DebugCmd} which simply echoes the given string.
 * <p>
 *     Provides a required second (third in context of its parent command) {@link TypedArg} which accepts
 *     a {@code String} to echo.
 * </p>
 * <p>
 *     Aliases: {@code echo}, {@code e}, {@code print}, {@code p}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     debug echo "hi"      - Prints hi
 *     dbg echo yo          - Prints yo
 *     d e huh              - Prints huh
 *     }</pre>
 * </p>
 * @see DebugCmd
 * @see Subcommand
 * @see TypedArg
 * @author Lehlogonolo Poole
 */
public class EchoCmd extends Subcommand {
    public EchoCmd() {
        super("echo", "Echoes the input string.");
        this.aliases("e", "print", "p").args(
                new TypedArg("message", "The message to echo", false, String.class),
                new TypedArg("boolean", "Boolean", true, Boolean.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || !(args[0] instanceof String message)) {
            logLabel.setText("Invalid arguments. Usage: echo <message: String>");
            return;
        }
        logLabel.setText(message);
        Static.getLog().println(message);
    }
}
