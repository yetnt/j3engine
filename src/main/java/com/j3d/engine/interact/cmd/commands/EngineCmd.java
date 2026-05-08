package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.dialog.AreYouSure;
import com.j3d.ui.util.SafeJLabel;

import java.util.ArrayList;

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

    public static class ExitCmd extends Subcommand {
        public ExitCmd() {
            super("exit", "Exits the program.");
            aliases("quit", "done", "bye", "close", "x").parseUsages();
        }

        @Override
        public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
            super.run(logLabel, aliasUsed, args, taggedArgs);
            AreYouSure ays = new AreYouSure(Static.mainFrame, true, "This will save nothing. Its a hard exit.");
            ays.setVisible(true);
            if (ays.canProceed())
                System.exit(0);
        }
    }
}
