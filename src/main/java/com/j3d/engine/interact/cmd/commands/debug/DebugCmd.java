package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.*;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A command for general logging and debugging. Just testing if new features work or otherwise quick
 * access to stuff.
 * <p>
 *     This is a command who's logic lies within the subcommands. This does not work but dispatching.
 *     See subcommand documentation for more detail on arguments and usage strings
 *     ({@link EchoCmd}, {@link TypeOf}, {@link RandomUUIDCmd}, {@link CameraInfoCmd})}
 * </p>
 * <p>
 *     Aliases: {@code debug}, {@code dbg}, {@code test}, {@code d}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     debug echo "hi"      - Prints "hi" to the console and to the command palette output label
 *     debug typeof 1       - Prints "Type: Integer" to the console and to the command palette output label
 *     debug id             - Retrieves and prints a random GObject's UUID (and copies to your clipboard)
 *     debug cam            - Prints camera position and rotation.
 *     }</pre>
 * </p>
 * @see Command
 * @see CommandsManager
 * @see EchoCmd
 * @see TypeOf
 * @see RandomUUIDCmd
 * @see CameraInfoCmd
 * @author Lehlogonolo Poole
 */
public class DebugCmd extends Command {
    public DebugCmd() {
        super("debug", "Just some debugging commands and stuff");
        this.aliases("dbg", "test", "d").args(
                new EchoCmd(),
                new TypeOf(),
                new RandomUUIDCmd(),
                new CameraInfoCmd(),
                new TriangleCmd()
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: debug <typeof|echo|id|cam> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }

}
