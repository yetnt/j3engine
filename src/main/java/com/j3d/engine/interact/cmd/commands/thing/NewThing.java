package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;
import com.j3d.engine.layer.Layer;

import java.util.ArrayList;

public class NewThing extends Subcommand {

    public NewThing() {
        super("new", "Creates a new Thing");
        this.args(
                new TypedArg("layerId", "The layer ID where the new thing will be added", true, String.class),
                new TypedArg("name", "The name of the new thing", true, String.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length >= 1 && !(args[0] instanceof String)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, String.class, String.class)[0]);
            return;
        }
        String name = "Thing";
        if (args.length > 1 && !(args[1] instanceof String)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, String.class, String.class)[0]);
            return;
        }
        Layer l = null;
        if (args.length == 1) {
            String layerId = (String) args[0];
            l = Static.renderer.layers.find(layerId);
        }
        new Thing(Static.renderer, l, "Thing");
        logLabel.setText("New Thing created" + (l != null ? " in layer " + l.getIdentifier() : " in the default layer"));
    }
}
