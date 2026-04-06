package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;


public class TranslateSelection extends Subcommand {

    TranslateSelection() {
        super("translate", "Translates the selection");
        this.aliases("t", "trans").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {

    }
}
