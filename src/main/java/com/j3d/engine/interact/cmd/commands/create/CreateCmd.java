package com.j3d.engine.interact.cmd.commands.create;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.Creator;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * A command to create various geometric objects in the scene.
 * <p>
 *      This command allows the user to specify a type of geometric object (e.g., cube, triangle, point, curve)
 *      to be created and added to the current scene. It leverages the {@link Creator} utility class
 *      to generate the geometry.
 * </p>
 * <p>
 *      The created object is automatically added to the currently active or a new usable {@link Layer}
 *      and given a unique name. For solid objects like cubes, it also calls {@link Thing#solidify()}.
 * </p>
 * Typical Usage:
 * <pre>{@code
 *      create cube
 *      create c
 *      create triangle
 *      create tri
 *      create point
 *      create p
 *      create curve
 *      create bezier
 * }</pre>
 * @author Lehlogonolo Poole
 * @see Command
 * @see Creator
 * @see Thing
 * @see Layer
 */
public class CreateCmd extends Command {

    private static final ArgSet argSet = new ArgSet(
            "object", "The object to create", false,
            "cube", "c",
            "triangle", "tri",
            "point", "p",
            "curve", "bezier"
    );

	public CreateCmd() {
        super("create", "Creates geometry!!!!!!!!!!!!!");
        this.aliases("object", "new").args(
                argSet
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String arg)) {
            logLabel.setText("Invalid arguments. Usage: create " + argSet.toUseString());
            return;
        }

        if (!argSet.isValid(arg)) {
            logLabel.setText("Invalid argument given. Usage: create " + argSet.toUseString());
            return;
        }

        Thing t = usableThing(arg);

        ArrayList<GObject> o = switch (arg) {
            case "cube", "c" -> Creator.cube();
            case "triangle", "tri" -> Creator.triangle();
            case "point", "p" -> Creator.point();
            case "curve", "bezier" -> Creator.curve();
            default -> new ArrayList<>();
        };

        if (o.isEmpty()) {
            // this wouldn't happen
            logLabel.setText("Invalid argument given. Usage: create " + argSet.toUseString());
            return;
        }

        t.addObjs(
                o.toArray(GObject[]::new)
        );

        if (arg.equals("c") || arg.equals("cube"))
            t.solidify();

        logLabel.setText(
                "Object created at (0, 0, 0)"
        );
    }

    public Thing usableThing(String name) {
        Layer usable = StaticRefs.getSceneManager().usableLayer();
        ArrayList<FindResult> results = StaticRefs.getSceneManager().finder.find(
                Thing.class,
                Finder.nameQuery(),
                name
        );
        String newName = name + results.size();

        return new Thing(usable, newName);
    }
}
