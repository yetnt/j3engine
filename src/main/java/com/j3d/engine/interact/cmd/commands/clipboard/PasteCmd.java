package com.j3d.engine.interact.cmd.commands.clipboard;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.buckets.Buckets;

import java.util.ArrayList;
import java.util.List;

public class PasteCmd extends Subcommand {
    public PasteCmd() {
        super(
                "paste",
                "Pastes a selection"
        );
        this.aliases("pt", "p").args(
                new TypedArg(
                        "severConnections", "Whether connections should be severed", true,
                        Boolean.class)
        ).addNoArgUsage().parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        ArrayList<GObject> copied = StaticRefs.getSceneManager().getClipboard();
        if (copied.isEmpty()) {
            logLabel.setText("Nothing to paste");
            return;
        }

        // get the arg

        boolean softDependencies = true;
        if (args.length > 0 && args[0] instanceof Boolean b) {
            softDependencies = b;
        }

        // set the stuff
        CopyProperties copyProperties = CopyProperties
                .builder(copied)
                .softDependencies(softDependencies)
                .build();

        // split
        ArrayList<ArrayList<GObject>> buckets =
                Buckets.of(copied, GPoint.class, GLine.class, GTri.class);

        try {
            for (ArrayList<GObject> bucket : buckets) {
                for (GObject obj : bucket) {
                    obj.copy(copyProperties);
                }
            }
        } catch (InvalidCopyException e) {
            // clean.
            copyProperties.getCopiesAsObjects().forEach(GObject::deleteSelf);
            logLabel.setText("Stopped paste due to invalid copy");
            return;
        }

        // gather all copied shiz
        ArrayList<GObject> copies = copyProperties.getCopiesAsObjects();

        // do smth idk, offset on the x?
        Vector3 offset = Vector3.X(10);

        Thing thing = new Thing(
                StaticRefs.getSceneManager(),
                StaticRefs.getSceneManager().usableLayer(),
                "copied" + copies.size()
                );

        Thing.moveObjects(copies, thing);

        VoidAction v = thing.translate(offset);

        v.run();
        // dont commit to history so only the user's movement gets commited.
//        SceneManager.history.add(v);

        StaticRefs.getHoverLabel().setText("Paste complete");

        // and now deselect everything.
        StaticRefs.getSceneManager().deselectAll();

        // select the thing
        StaticRefs.getSceneManager().select(thing);

        // and now run translate!
        StaticRefs.getCommandParser().runCommand(
                CommandsManager.commands.transform,
                "transform",
                new ArrayList<>(List.of("translate","p")),
                new ArrayList<>(List.of(
                        new TaggedArgValue<>("copied").setName("string").setType(String.class)
                ))
        );
    }
}
