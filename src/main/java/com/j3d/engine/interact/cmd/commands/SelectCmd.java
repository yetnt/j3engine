package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

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
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
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
