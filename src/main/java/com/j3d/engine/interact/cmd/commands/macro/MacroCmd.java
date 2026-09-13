package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class MacroCmd extends Command {
    public MacroCmd() {
        super("macro", "Handle user defined macros");
        this.args(
                new EndCmd(),
                new RecordCmd()
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length > 0) {
           dispatchToSubcommands((String)args[0], logLabel, args, taggedArgs);
        }
    }
}
