package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TypedArg;

import javax.swing.*;

import static com.j3d.engine.interact.cmd.commands.selection.SelectionCmd.selectDispatch;

public class TranslateSelection extends Subcommand {

    TranslateSelection() {
        super("translate", "Translates the selection");
        this.aliases("t", "trans").parseUsages();
    }

    @Override
    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        selectDispatch(SC_Option.TRANSLATE);
    }
}
