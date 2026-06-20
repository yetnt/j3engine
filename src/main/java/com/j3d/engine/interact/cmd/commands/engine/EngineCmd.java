package com.j3d.engine.interact.cmd.commands.engine;

import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A command for managing engine properties or otherwise quickly doing engine effect stuff.
 * access to stuff.
 * <p>
 *     This is a command who's logic lies within the subcommands. This does not work but dispatching.
 *     See subcommand documentation for more detail on arguments and usage strings
 *     ({@link ExitCmd})
 * </p>
 * <p>
 *     Aliases: {@code engine}, {@code eng}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     engine exit       - Initiate shutdown sequence.
 *     }</pre>
 * </p>
 * @see Command
 * @see CommandsManager
 * @see ExitCmd
 * @author Lehlogonolo Poole
 */
public class EngineCmd extends Command {
    public EngineCmd() {
        super("engine", "Engine related commands");
        this.aliases("eng").args(
                new ExitCmd()
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: debug <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }

}
