package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * A command which selects a given {@link GObject}
 * <p>
 *     Provides a required {@link TypedArg} which accepts an {@link Any} type, but expects a {@link GObject}
 *     (representing a {@link GObject}'s name or UUID)
 * </p>
 * <p>
 *     Aliases: {@code select}, {@code s}, {@code sel}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     s 123e4567-e89b-12d3-a456-426614174000 - Selects the object with the given UUID
 *     }</pre>
 * </p>
 * @see Command
 * @see TypedArg
 * @see GObject
 * @author Lehlogonolo Poole
 */
public class SelectCmd extends Command {
    public SelectCmd() {
        super(
                "select",
                "Selects objects"
        );
        this.aliases("s", "sel").args(
                new TypedArg(
                        "argument",
                        "something to select",
                        false,
                        Any.class
                )
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1) {
            logLabel.setText(
                    "Usage: " + aliasUsed + " <any>"
            );
        }
        Object q = args[0];
        if (!(q instanceof GObject gobject)) {
            logLabel.setText(
                    "Select GObjects man."
            );
            return;
        }
        StaticRefs.getSceneManager().select(gobject);
    }
}
