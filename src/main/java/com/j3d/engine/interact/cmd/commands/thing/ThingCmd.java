package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.interact.cmd.base.Command;

import javax.swing.JLabel;

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
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: thing <subcommand> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args);
    }

}
