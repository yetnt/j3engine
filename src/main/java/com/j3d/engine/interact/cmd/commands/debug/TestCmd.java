package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.dialog.JKeyChooser;

import java.util.ArrayList;

public class TestCmd extends Subcommand {

    public TestCmd() {
        super("test", "Testing command");
        this.addNoArgUsage().parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);

        JKeyChooser keyChooser = new JKeyChooser(StaticRefs.getMainFrame());

        logLabel.setText(keyChooser.getFinalKeyStroke().toString());
    }
}
