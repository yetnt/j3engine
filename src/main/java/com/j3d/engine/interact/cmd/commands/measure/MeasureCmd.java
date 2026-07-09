package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 *  A command that dispatches to various measurement subcommands like {@link VolumeCmd} and {@link DistanceCmd}.
 * This command acts as a parent for all measurement-related operations.
 * <p>
 *     Aliases: {@code measure}, {@code meas}, {@code m}
 * </p>
 * @see VolumeCmd
 * @see DistanceCmd
 * @author Lehlogonolo Poole
 */
public class MeasureCmd extends Command {
    public MeasureCmd() {
        super("measure", "Measures geometry");
        this.aliases("meas", "m")
                .args(new VolumeCmd(), new DistanceCmd()).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1) {
            logLabel.setText("No subcommand?");
            return;
        }
        String alias = (String) args[0];
        dispatchToSubcommands(alias, logLabel, args, taggedArgs);
    }
}
