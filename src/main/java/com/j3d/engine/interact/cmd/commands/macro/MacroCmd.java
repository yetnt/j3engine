package com.j3d.engine.interact.cmd.commands.macro;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A dispatcher command for macro related things.
 * <p>
 *     This class does functionally nothing other than dispatch
 * </p>
 * <p>
 *     Aliases: {@code macro}, {@code auto}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     macro record poop    // Record poop macro
 *     macro end poop       // End recording for the poop macro
 *     macro list           // Opens the directories where the macros live
 *     macro run poop       // Runs the poop macro
 *     }</pre>
 * </p>
 */
public class MacroCmd extends Command {

    public MacroCmd() {
        super("macro", "Handle user defined macros");
        this.aliases("auto").args(
                new RecordCmd(),
                new EndCmd(),
                new ListCmd(),
                new RunCmd()
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
