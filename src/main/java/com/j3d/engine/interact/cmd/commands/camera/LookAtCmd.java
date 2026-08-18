package com.j3d.engine.interact.cmd.commands.camera;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * A subcommand of {@link CameraCmd} which makes the camera look at a given {@link Thing} or {@link Vector3}
 * <p>
 *     Provides a required {@link TypedArg} which accepts either a {@link String} (representing a {@link Thing}'s
 *     name or UUID) or a {@link Vector3} (representing a point in 3D space)
 * </p>
 * <p>
 *     Aliases: {@code lookAt}, {@code la}, {@code look}, {@code lookat}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     cam lookat "myObject"            - Makes the camera look at the thing named "myObject"
 *     cam la (10, 20, 30)              - Makes the camera look at the point (10, 20, 30)
 *     cam look 123e4567-e89b-12d3-a456-426614174000 - Makes the camera look at the thing with the given UUID
 *     }</pre>
 * </p>
 * @see Command
 * @see TypedArg
 * @see Thing
 * @see Vector3
 * @author Lehlogonolo Poole
 */
public class LookAtCmd extends Subcommand {
    public LookAtCmd() {
        super("lookAt", "Looks at a specific position or Thing");
        this.aliases("la", "look", "lookat").args(
                new TypedArg("thing", "the name or UUID of the thing to look at or otherwise coordinates.", false,
                        String.class, Vector3.class, Thing.class)
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || (!(args[0] instanceof String) && !(args[0] instanceof Vector3) && !(args[0] instanceof Thing))) {
            HashSet<GObject> selected = StaticRefs.getSceneManager().getSelected();
            if (selected.isEmpty()) {
                logLabel.setText("Invalid arguments. Usage: lookat <thing: String|Vector3|UUID> (or select something first)");
                return;
            }
            ArrayList<Vector3> allCentres
                    = selected.stream()
                    .map(GObject::getPivot)
                    .collect(Collectors.toCollection(ArrayList::new));
            Vector3 centroid =
                    Vector3.reduceToVector3(allCentres,Vector3::add)
                            .div(allCentres.size());

            StaticRefs.getCamera().lookAt(centroid);
            logLabel.setText("Looked at " + centroid.toCommandPaletteString() + " (centroid of " + selected.size() + " objects)");
            return;
        }

        switch (args[0]) {
            case String thing -> {
                Thing t = StaticRefs.getSceneManager().findThing(thing);
                if (t == null) {
                    logLabel.setText("No thing found with the name " + thing);
                    return;
                }

                StaticRefs.getCamera().lookAt(t.getCentroid());

                logLabel.setText("Looked at " + t.getName());
            }
            case Vector3 v3 -> {
                StaticRefs.getCamera().lookAt(v3);
                logLabel.setText("Looked at " + v3.toCommandPaletteString());
            }
            case Thing thing -> {
                StaticRefs.getCamera().lookAt(thing.getCentroid());
                logLabel.setText("Looked at " + thing.getName());
            }
            default -> {
            }
        }
    }
}
