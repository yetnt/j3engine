package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.KeyedStatefulCommand;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.scene.nodes.util.Sampler;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.tuple.Pair;
import com.j3d.utility.generic.tuple.SamePair;
import com.j3d.utility.generic.tuple.Triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.j3d.StaticRefs.getSceneManager;

/**
 * A command to extrude a selected {@link GTri} (triangle) along its normal vector.
 * <p>
 *      This command allows the user to interactively adjust the extrusion length and direction
 *      of a single selected triangle. A visual ghost of the extruded shape is displayed
 *      during the process.
 * </p>
 * <p>
 * Users can commit the extrusion with 'Enter' or cancel with 'Escape'.
 * The extrusion length can be adjusted using the 'Up' and 'Down' arrow keys.
 * The direction of extrusion (flipped along the normal) can be toggled using the
 * 'Left' and 'Right' arrow keys.
 * A "gear" system, activated by the 'R' key, controls the step size of length adjustments.
 * </p>
 * Typical Usage:
 *  <pre>{@code
 *  // Select a single triangle first, then run the command
 *  extrude
 *  // Or specify the triangle directly by its UUID
 *  extrude a7e58b1c-2d3f-4a5b-6c7d-8e9f0a1b2c3d
 *  }</pre>
 *
 * @author Lehlogonolo Poole
 * @see Command
 * @see GTri
 * @see KeyedStatefulCommand
 */
public class ExtrudeCmd extends Command implements KeyedStatefulCommand {

    /** An array of step sizes (e.g., 1, 5, 20) that the user can cycle through. */
    protected double[] gearTrain = new double[]{1, 5, 20, 0.01};
    /** The index of the currently active step size in the gearTrain. */
    protected int currentIndex = 0;
    /** The keybinding used to cycle through the gearTrain step sizes. */
    protected J3Key gear;    /** A list of temporary keybindings (e.g., arrow keys) active during this state. */
    protected ArrayList<J3Key> keys = new ArrayList<>();

    protected GTri tri;

    double length = 1;
    boolean flipped = false;
    UUID overlapId = UUID.randomUUID();

    public ExtrudeCmd() {
        super("extrude", "Extrude the given triangle along it's normal.");
        this.aliases("ex", "pull", "extr").args(
                new TypedArg(
                        "tri", "Triangle to extrude",
                        true, GTri.class)
        ).parseUsages();

        gear = newGearKey("extrude");
        getKeys().add(gear);

        setUpKey(
                ()->null,
                n->false,
                o-> {
                    // increase length
                    length += gearTrain[currentIndex];
                }
        );
        setDownKey(
                ()->null,
                n->false,
                o-> {
                    // decrease length
                    if (length-gearTrain[currentIndex] < 0) return;
                    length -= gearTrain[currentIndex];
                }
        );
        setLeftKey(
                ()->null,
                n->false,
                o-> {
                    flipped = !flipped;
                }
        );
        setRightKey(
                ()->null,
                n->false,
                o-> {
                    flipped = !flipped;
                }
        );
    }


    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        // do validation and shi
        if (args.length > 0) {
            if (!(args[0] instanceof GTri)) {
                logLabel.setText("The argument must be a triangle");
                CommandsManager.clearCurrent();
                return;
            }
            tri = (GTri) args[0];
        } else {
            ArrayList<GTri> tris
                    = StaticRefs.getSceneManager().getSelected()
                    .stream().filter(GTri.class::isInstance)
                    .map(GTri.class::cast).collect(Collectors.toCollection(ArrayList::new));
            if (tris.size() != 1) {
                logLabel.setText("Make sure your selection contains at most a single triangle (Currently " + tris.size() + ")");
                CommandsManager.clearCurrent();
                return;
            }
            tri = tris.getFirst();
        }

//        keys.add(gear);
        run(this, "extrude", null, logLabel);
    }

    @Override
    public void onStart(Void object, SafeJLabel label) {
        keys.forEach(StaticRefs.getGlobalKeybinds()::registerJ3Key);

        Consumer<Graphics2D> drawGhosts = g -> {
            g.setColor(Color.WHITE);

            // drawing ghost

            ghost(g);


            label.setText(
                    "Extruding triangle with a length of "+SafeJLabel.EMPH+" "+SafeJLabel.EMPH+" using arrow keys. "
                            +"| (Click "+SafeJLabel.EMPH+" to change length step)",

                    new JLabelRichText(length+"")
                            .font(J3DTheme.TEXT_SECONDARY.color().darker(), "6"),

                    new JLabelRichText("("+getGearTrain()[currentIndex]+")")
                            .font(J3DTheme.TEXT_SECONDARY.color().darker(), "6"),
                    "[R]"
            );
        };

        getSceneManager().scheduleOverlap(overlapId, drawGhosts);
    }

    private void ghost(Graphics2D graphics2D) {
        Pair<Triple<GPoint>, Triple<Vector3>> newPoints = getAllPoints();
        Triple<Vector3> original = newPoints.first.map(GPoint::toPoint);
        Triple<Vector3> extruded = newPoints.second;

        // join the points
        Sampler.joinNGonArbitaryVectors(extruded.toArrayList(), graphics2D);

        Triple.forEachPair(
                original,
                extruded,
                (v1, v2) -> {
                    getSceneManager().drawLine3D(
                            graphics2D, v1, v2, StaticRefs.getCamera()
                    );
                }
        );
    }

    private Pair<Triple<GPoint>, Triple<Vector3>> getAllPoints() {
        // get the normal of the tri
        Vector3 normal = tri.getWinding().normal();
        // get the points
        ArrayList<GPoint> points = tri.getWinding()
                .stream()
                .collect(Collectors.toCollection(ArrayList::new));

        Vector3 delta = normal.mult(flipped ? -length : length);

        // new points
        ArrayList<Vector3> newPoints = points
                .stream()
                .map(GPoint::getPivot)
                .map(v -> v.add(delta))
                .collect(Collectors.toCollection(ArrayList::new));

        return new Pair<>(
                new Triple<>(points.getFirst(), points.get(1), points.getLast()),
                new Triple<>(newPoints.getFirst(), newPoints.get(1), newPoints.getLast())
        );

    }

    private void join() {
        Pair<Triple<GPoint>, Triple<Vector3>> newPoints = getAllPoints();

        ArrayList<GPoint> original = newPoints.first.toArrayList();
        ArrayList<GPoint> extruded = newPoints.second.map(GPoint::new).toArrayList();

        HashSet<GLine> lines = new HashSet<>(tri.getLegStream().toList());
        HashSet<GTri> tris = new HashSet<>();

        for (int i = 0; i < original.size(); i++) {
            //bottom face points
            GPoint A = original.get(i);
            GPoint B = original.get((i + 1) % original.size()); // Connect last point to first
            //top face points
            GPoint D = extruded.get(i);
            GPoint C = extruded.get((i + 1) % extruded.size()); // Connect last point to first
            // edge lines
            GLine AB = GLine.getInstance(lines, A, B); // this should already be within the original triangle.
            GLine BC = GLine.getInstance(lines, B, C);
            GLine CD = GLine.getInstance(lines, C, D);
            GLine DA = GLine.getInstance(lines, D, A);
            GLine diagonalBD = new GLine(B, D);
            diagonalBD.setColour(
                    new Color(0, 0, 0, 40)
            );

            // Triangle ABD and CDB
            Color col = tri.getColour();
            lines.addAll(
                    List.of(
                            AB, BC, CD, DA, diagonalBD
                    )
            );
            if (!flipped) {
                tris.add(new GTri(col, AB, diagonalBD, DA, new Winding(D, B, A)));
                tris.add(new GTri(col, BC, diagonalBD, CD, new Winding(B, D, C)));
            } else {
                tris.add(new GTri(col, AB, diagonalBD, DA, new Winding(A, B, D)));
                tris.add(new GTri(col, BC, diagonalBD, CD, new Winding(C, D, B)));
            }
        }

        // connect the top face.
        GLine l1 = GLine.getInstance(lines, extruded.getFirst(), extruded.get(1));
        GLine l2 = GLine.getInstance(lines, extruded.get(1), extruded.getLast());
        GLine l3 = GLine.getInstance(lines, extruded.getLast(), extruded.getFirst());
        lines.addAll(List.of(l1, l2, l3));

        tris.add(
                new GTri(
                        tri.getColour(),
                        l1, l2, l3,
                        new Winding(extruded.getFirst(), extruded.get(1), extruded.getLast())
                )
        );

        Thing t = StaticRefs.getSceneManager().findObjectParent(original.getFirst());

        t.addObjs(original.toArray(new GPoint[0]))
                .addObjs(extruded.toArray(new GPoint[0]))
                .addObjs(lines.toArray(new GLine[0]))
                .addObjs(tris.toArray(new GTri[0]));
    }

    @Override
    public void onEnter(ActionEvent e, Void object, SafeJLabel label) {
        if (length < 0.01) {
            KeyedStatefulCommand.super.onEsc(e, object, label);
            label.setText("Length is less than 0.01");
            finish(label);
            return;
        }
        KeyedStatefulCommand.super.onEnter(e, object, label);
        join();
        finish(label);
    }

    @Override
    public void onEsc(ActionEvent e, Void object, SafeJLabel label) {
        KeyedStatefulCommand.super.onEsc(e, object, label);
        finish(label);
    }

    private void finish(SafeJLabel label) {
        length = 1;
        flipped = false;
        tri = null;
        getSceneManager().removeOverlap(overlapId);
        label.clear();
        keys.forEach(key -> StaticRefs.getGlobalKeybinds().removeJ3Key(key.getId()));
    }

    @Override
    public ArrayList<J3Key> getKeys() {
        return keys;
    }

    @Override
    public String selfName() {
        return "extrude";
    }

    @Override
    public double[] getGearTrain() {
        return gearTrain;
    }

    @Override
    public int getGearIndex() {
        return currentIndex;
    }

    @Override
    public void setGearIndex(int index) {
        currentIndex = index;
    }
}
