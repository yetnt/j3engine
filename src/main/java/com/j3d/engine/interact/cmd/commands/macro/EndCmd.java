package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.macros.Macro;
import com.j3d.engine.interact.macros.MacroLine;
import com.j3d.engine.interact.macros.MacroRecorder;
import com.j3d.ui.SafeJLabel;

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
            ArrayList<MacroLine> macroLines = mr.stop();
            String name = mr.getName();
            if (macroLines.getLast().instruction().contains("macro"))
                macroLines.removeLast();
            try {
                StaticRefs.getEngineFiles().macrosFile.write(name, macroLines);
                StaticRefs.getMacroUtils().addMacro(name, new Macro(name, macroLines));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
