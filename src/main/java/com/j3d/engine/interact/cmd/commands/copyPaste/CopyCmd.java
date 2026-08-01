package com.j3d.engine.interact.cmd.commands.copyPaste;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.HashSet;

public class CopyCmd extends Command {

    public CopyCmd() {
        super(
                "copy",
                "Copies a selection"
        );
        this.aliases("cp", "c").noArgs().parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        HashSet<GObject> selected = StaticRefs.getSceneManager().getSelected();
        if (selected.isEmpty()) {
            logLabel.setText("Nothing to copy");
            return;
        }

        logLabel.setText("Copied " + selected.size() + " objects");

        StaticRefs.getSceneManager().setCopied(new ArrayList<>(selected));
    }
}
