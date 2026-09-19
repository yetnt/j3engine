package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.SafeJLabel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ListCmd extends Subcommand {

    public ListCmd() {
        super("list", "Lists all available macros. With an optional param to open the folder.");
        this.aliases("l", "all").parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);

        try {
            Desktop.getDesktop().open(StaticRefs.getEngineFiles().macrosFile.getROOT().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
