package com.j3d.engine.interact.cmd.commands.clipboard;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class ClipboardCmd extends Command {
    public ClipboardCmd() {
        super("clipboard", "Manages clipboard operations (copy, paste, cut).");
        this
                .aliases("cb", "clip", "cl")
                .args(new CopyCmd(), new PasteCmd())
                .parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1) {
            logLabel.setText("Clipboard command requires arguments");
            return;
        }

        if (!(args[0] instanceof String s)) {
            logLabel.setText("Clipboard command requires a subcommand gng");
            return;
        }

        dispatchToSubcommands(
                s,
                logLabel,
                args, taggedArgs
        );
    }
}
