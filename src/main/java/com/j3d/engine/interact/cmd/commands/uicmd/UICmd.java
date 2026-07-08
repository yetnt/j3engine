package com.j3d.engine.interact.cmd.commands.uicmd;

import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 *  A command for interacting with and managing the User Interface (UI) elements of the engine.
 * <p>
 *     This command primarily dispatches to its subcommands for specific UI operations.
 *     See subcommand documentation for more detail on arguments and usage strings
 *     ({@link ToggleCmd})}
 * </p>
 * <p>
 *     Aliases: {@code ui}, {@code gui}, {@code swing}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     ui toggle layertree  - Toggles the visibility of the Layer Tree panel.
 *     gui t history        - Toggles the visibility of the History panel.
 *     swing toggle debug   - Toggles the visibility of the Debug panel.
 *     }</pre>
 * </p>
 * @see Command
 * @see ToggleCmd
 * @author Lehlogonolo Poole
 */
public class UICmd extends Command {

    public UICmd() {
        super("ui", "Toggle ui related options.");
        this.args(new ToggleCmd()).aliases("gui", "swing").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: ui <typeof|echo|id|cam> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }
}
