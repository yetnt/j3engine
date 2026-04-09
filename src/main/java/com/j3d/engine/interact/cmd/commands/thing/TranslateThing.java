package com.j3d.engine.interact.cmd.commands.thing;

import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;

import java.util.ArrayList;

class TranslateThing extends Subcommand {

    public TranslateThing() {
        super("trans", "Translates a Thing");
        this.args(
                new TypedArg("thing", "The thing to translate", false, Thing.class),
                new TypedArg("vector3", "The translation vector", false, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length != 2 || !(args[0] instanceof Thing t) || !(args[1] instanceof Vector3 v)) {
            logLabel.setText("Invalid arguments. Usage:" + returnUsagesWhere(aliasUsed, Thing.class, Vector3.class)[0]);
            return;
        }
        t.translate(v);
        logLabel.setText("Thing translated by " + v);
    }
}
