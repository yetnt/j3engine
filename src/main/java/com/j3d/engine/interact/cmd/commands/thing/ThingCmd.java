package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class ThingCmd extends Command {
    public ThingCmd() {
        super("thing", "thing");
        aliases("t").args(
                new ToggleVisCmd()
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: thing <toggle> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }
}
