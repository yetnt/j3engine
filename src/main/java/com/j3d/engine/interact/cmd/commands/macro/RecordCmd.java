package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.macros.MacroRecorder;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

public class RecordCmd extends Subcommand {
    public RecordCmd() {
        super("record", "Record a new macro");
        this.args(
                new TypedArg(
                        "macroName", "The macro name",
                        false, String.class
                )
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1) {
            logLabel.setText("Not enough arguments given. Usage: "
                    + aliasUsed + " "
                    + getUsages().stream().findAny().orElse(""));
            return;
        }

        String name = (String) args[0];

        MacroRecorder mr = CommandsManager.commands.getMacroRecorder();

        if (mr.isRecording()) {
            logLabel.setText("A macro is already being recorded! (This will be ignored)");
            return;
        }

        mr.record(name);
    }
}
