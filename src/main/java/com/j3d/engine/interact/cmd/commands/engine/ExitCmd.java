package com.j3d.engine.interact.cmd.commands.engine;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.dialog.AreYouSure;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A subcommand of {@link EngineCmd} which simply exits the entire app.
 * <p>
 *     Provides an optional first (second in context of its parent command) {@link TypedArg} which accepts
 *     a {@code boolean} which defaults to false. If true, then no confirmation is made before shut down
 *     possibly resulting in data loss.
 * </p>
 * <p>
 *     Aliases: {@code exit}, {@code quit}, {@code done}, {@code bye}, {@code close}, {@code x}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     engine quit      - Asks user if they're sure before quitting
 *     engine close yes - Force closes the app
 *     engine x         - Asks user if they're sure before quitting
 *     eng done true    - Force closes the app
 *     }</pre>
 * </p>
 * @see EngineCmd
 * @see Subcommand
 * @see TypedArg
 * @author Lehlogonolo Poole
 */
public class ExitCmd extends Subcommand {
    public ExitCmd() {
        super("exit", "Exits the program.");
        aliases("quit", "done", "bye", "close", "x").args(
                new TypedArg("force", "Force shutdown the engine by skipping the are you sure dialog",
                        true, Boolean.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length > 0 && !(args[0] instanceof Boolean)) {
            logLabel.setText("Invalid arguments. Usage: exit [force: Boolean]");
            return;
        }
        boolean force = args.length != 0 && (boolean) args[0];
        if (!force) {
            AreYouSure ays = new AreYouSure(StaticRefs.getMainFrame(), true, "This will save nothing. Its a hard exit.");
            ays.setVisible(true);
            if (ays.canProceed())
                System.exit(0);
        } else System.exit(0);
    }
}
