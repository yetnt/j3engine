package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.SafeJLabel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ToggleVisCmd extends Subcommand {
    public ToggleVisCmd() {
        super("toggle-vis", "toggle-vis");
        aliases("t", "v", "tv", "toggle").parseUsages();
    }

    @Override
    public void run(@NotNull Invoker invoker, SafeJLabel logLabel, String aliasUsed, @NotNull Object[] args, @NotNull ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
    }
}
