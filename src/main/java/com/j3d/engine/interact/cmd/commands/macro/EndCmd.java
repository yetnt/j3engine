package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.macros.Macro;
import com.j3d.engine.interact.macros.MacroRecorder;
import com.j3d.ui.SafeJLabel;
import com.yetnt.utils.builders.InlineHTML;

import java.io.IOException;
import java.util.ArrayList;

public class EndCmd extends Subcommand {
    public EndCmd() {
        super("end", "End a recording macro");
        this.aliases("stop", "finish").addNoArgUsage().parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);

        MacroRecorder mr = StaticRefs.getMacroUtils().getMacroRecorder();

        if (mr.isRecording()) {
            Macro macro = mr.stop();
            if (macro.getMacroLines().getLast().instruction().contains("macro"))
                macro.getMacroLines().removeLast();
            try {
                StaticRefs.getMacroUtils().addMacro(macro);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        logLabel.setText("Recording macro finished. Enter " + SafeJLabel.EMPH + " to edit the keybind to this macro.", new InlineHTML("ALT+M").bold().italic());


    }
}
