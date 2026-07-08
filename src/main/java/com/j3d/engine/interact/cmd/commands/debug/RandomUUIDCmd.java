package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.ClipboardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A no-arg subcommand of {@link DebugCmd} which simply fetches a random UUID string representing a {@link GObject}
 * that exists within the scene and copies it to the clipboard
 * <p>
 *     Aliases: {@code id}, {@code rand}, {@code random}, {@code r}, {@code uuid}, {@code u}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     debug id     - Prints a UUID
 *     dbg r        - Prints a UUID
 *     debug uuid   - Prints a UUID
 *     You get the point.
 *     }</pre>
 * </p>
 * @see DebugCmd
 * @see Subcommand
 * @see ClipboardUtil
 * @see GObject
 * @author Lehlogonolo Poole
 */
public class RandomUUIDCmd extends Subcommand {
    public RandomUUIDCmd() {
        super("id", "returns a random object uuid");
        aliases("rand", "random", "r", "uuid", "u")
                .args(
                        new TypedArg("bool", "random bool so this doesnt clash with another subcommand",
                                true, Boolean.class)
                )
                .parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        List<GObject> objects = Static.sceneManager.layers
                .stream()
                .flatMap(Layer::stream)
                .flatMap(Thing::objectsStream)
                .toList();

        GObject random = objects.get(new Random().nextInt(objects.size()));
        logLabel.setText(random.getId().toString());
        ClipboardUtil.copyToClipboard(random.getId().toString());
    }
}
