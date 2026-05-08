package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.ui.util.SafeJLabel;

import java.util.ArrayList;

public class ToggleVisCmd extends Subcommand {
    public ToggleVisCmd() {
        super("toggle-vis", "toggle-vis");
        aliases("t", "v", "tv", "toggle").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);

    }
}
