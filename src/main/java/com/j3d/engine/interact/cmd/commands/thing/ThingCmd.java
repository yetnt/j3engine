package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;

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

        this.usages.put(
                new ArrayList<>(List.of(Thing.class, Vector3.class)),
                "[scale|translate] <Thing> (vector3) ...key:value");
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: thing <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }

}
