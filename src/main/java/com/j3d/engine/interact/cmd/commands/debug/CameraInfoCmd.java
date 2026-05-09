package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.utility.ClipboardUtil;

import java.util.ArrayList;

/**
 * A subcommand of {@link DebugCmd} which simply prints camera properties
 * <p>
 *     Provides an optional second (third in context of its parent command) required {@link ArgSet} which accepts
 *     the following strings: {@code "position"}, {@code "rotation"}, {@code "pos"}, {@code "rot"}, {@code "p"}, {@code "r"}
 * </p>
 * <p>
 *     Aliases: {@code camera}, {@code cam}, {@code view}, {@code c}, {@code v}
 * </p>
 * <p>
 *     (For the below usage, say we have a camera at {@code (X:10, Y:2, Z:2)} with a rotation of {@code (Yaw:0, Pitch:60, Roll:20)})
 *     <pre>{@code
 *     debug cam            - pos(10, 2, 2) rot(0, 60, 20)
 *     dbg view rot         - rot(0, 60, 20)
 *     d cam pos            - pos(10, 2, 2)
 *     dbg c                - pos(10, 2, 2) rot(0, 60, 20)
 *     debug cam p          - pos(10, 2, 2)
 *     dbg camera position  - pos(10, 2, 2)
 *     }</pre>
 * </p>
 * @see DebugCmd
 * @see Subcommand
 * @see ArgSet
 * @author Lehlogonolo Poole
 */
public class CameraInfoCmd extends Subcommand {
    public ArgSet argSet =
            new ArgSet("type", "The type of info to return", true,
                    "pos", "rot", "position", "rotation", "p", "r");

    public CameraInfoCmd() {
        super("camera", "Prints camera information to the console.");
        aliases("cam", "c", "view", "v").args(
                argSet
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length > 0 && !(args[0] instanceof String)) {
            logLabel.setText("Invalid arguments. Usage: debug " + aliasUsed + " " + argSet.toUseString());
            return;
        }

        String rot = "rot(" +
                Static.camera.getRotation().getPitch() + ", "
                + Static.camera.getRotation().getYaw() + ", "
                + Static.camera.getRotation().getRoll() + ")";
        String pos = "pos(" +
                Static.camera.getPosition().getX() + ", "
                + Static.camera.getPosition().getY() + ", "
                + Static.camera.getPosition().getZ() + ")";

        String content = args.length == 0 ?
                pos + " " + rot : switch ((String)args[0]) {
            case "pos", "p", "position" -> pos;
            case "rot", "r", "rotation" -> rot;
            default -> pos + " " + rot;
        };

        logLabel.setText(content);
        ClipboardUtil.copyToClipboard(content);
        Static.getLog().println(content);
    }

}
