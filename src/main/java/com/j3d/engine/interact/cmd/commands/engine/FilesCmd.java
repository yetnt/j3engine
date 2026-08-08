package com.j3d.engine.interact.cmd.commands.engine;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.SafeJLabel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A subcommand of {@link EngineCmd} which opens the J3Engine folder in the file explorer.
 * <p>
 *     Aliases: {@code files}, {@code l}, {@code locate}, {@code folder}, {@code f}, {@code location}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     engine files    - Opens the J3Engine folder
 *     eng f           - Opens the J3Engine folder
 *     }</pre>
 * </p>
 * @see EngineCmd
 * @see Subcommand
 * @author Lehlogonolo Poole
 */
public class FilesCmd extends Subcommand {
    public FilesCmd() {
        super("files", "Opens the J3Engine folder");
        aliases("l", "locate", "folder", "f", "location").addNoArgUsage().parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        try {
            Desktop.getDesktop().open(StaticRefs.getEngineFiles().getEngineFolder());
        } catch (IOException e) {
            logLabel.setText("Something went wrong tryna open the file...");
        }
    }
}
