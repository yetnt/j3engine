package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.macros.MacroRecorder;
import com.j3d.engine.interact.macros.MacroRunner;
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.util.ArrayList;

public class RunCmd extends Subcommand {
    public RunCmd() {
        super("run", "Run a new macro");
        this.aliases("start", "begin").args(
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

        MacroRecorder mr = StaticRefs.getMacroUtils().getMacroRecorder();
        MacroRunner mr2 = StaticRefs.getMacroUtils().getMacroRunner();

        if (mr.isRecording()) {
            return;
        }

        if (!StaticRefs.getMacroUtils().getMacros().containsKey(name)) {
            logLabel.setText("No macro with the name " + SafeJLabel.EMPH + " exists!", new JLabelRichText(name).bold().italic());
        }

        StaticRefs.getHoverLabel().setText(
                "Running macro " + SafeJLabel.EMPH + ".",
                new JLabelRichText(name).bold().italic()
        );

        mr2.run(name);
    }
}
