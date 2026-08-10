package com.j3d.engine.interact.cmd.commands.camera;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.camera.orbit.OrbitCmd;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A command that groups all camera-related subcommands.
 * <p>
 *     This command acts as a dispatcher for various camera operations such as
 *     orbiting, looking at specific objects, or teleporting the camera.
 *     It provides a central point for managing camera interactions within the engine.
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     camera orbit <args>
 *     c orbit <args>
 *     camera lookat <args>
 *     v teleport <args>
 *     }</pre>
 * </p>
 * @author Lehlogonolo Poole
 * @see Command
 * @see OrbitCmd
 * @see LookAtCmd
 * @see TeleportCmd
 */
public class CameraCmd extends Command {

    public CameraCmd() {
        super("camera", "Camera commands");
        this.aliases("c", "cam", "view", "v").args(
                new CameraInfoCmd(),
                new OrbitCmd(),
                new LookAtCmd(),
                new TeleportCmd()
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String subcommandName)) {
            logLabel.setText("Invalid arguments. Usage: debug <typeof|echo|id|cam> ...");
            return;
        }
        dispatchToSubcommands(subcommandName, logLabel, args, taggedArgs);
    }
}
