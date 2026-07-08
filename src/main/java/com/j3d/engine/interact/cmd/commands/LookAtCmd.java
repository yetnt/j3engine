package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;

import java.util.ArrayList;

/**
 * A command which makes the camera look at a given {@link Thing} or {@link Vector3}
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
 *     lookat "myObject"            - Makes the camera look at the thing named "myObject"
 *     la (10, 20, 30)              - Makes the camera look at the point (10, 20, 30)
 *     look 123e4567-e89b-12d3-a456-426614174000 - Makes the camera look at the thing with the given UUID
 *     }</pre>
 * </p>
 * @see Command
 * @see TypedArg
 * @see Thing
 * @see Vector3
 * @author Lehlogonolo Poole
 */
public class LookAtCmd extends Command {
    public LookAtCmd() {
        super("lookAt", "Looks at a specific position or Thing");
        this.aliases("la", "look", "lookat").args(
                new TypedArg("thing", "the name or UUID of the thing to look at or otherwise coordinates.", false,
                        String.class, Vector3.class, Thing.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || (!(args[0] instanceof String) && !(args[0] instanceof Vector3) && !(args[0] instanceof Thing))) {
            logLabel.setText("Invalid arguments. Usage: lookat <thing: String|Vector3|UUID>");
            return;
        }

        switch (args[0]) {
            case String thing -> {
                Thing t = Static.sceneManager.findThing(thing);
                if (t == null) {
                    logLabel.setText("No thing found with the name " + thing);
                    return;
                }

                Static.camera.lookAt(t.getCentroid());
            }
            case Vector3 v3 -> Static.camera.lookAt(v3);
            case Thing thing -> Static.camera.lookAt(thing.getCentroid());
            default -> {
            }
        }
    }
}
