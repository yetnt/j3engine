package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo3d.AxisPlane;
import com.j3d.engine.geometry.geo3d.Sampler;
import com.j3d.engine.geometry.geo3d.Solids;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.KeyedStatefulCommand;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.SamePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j3d.StaticRefs.getSceneManager();

/**
 * A command to create a prism.
 * <p>
 * This command allows the user to define a prism by specifying the centre points and
 * defining planes for its top and bottom faces. It als allows interactive adjustment
 * of the prism's radius and number of sides using arrow keys, and provides a "gear"
 * system to control the step size of radius adjustments.
 * </p>
 * <p>
 * The command operates in a stateful manner, displaying a visual ghost of the prism
 * as it's being defined. Users can commit the prism creation with 'Enter' or cancel
 * with 'Escape'.
 * </p>
 * Typical Usage:
 *  <pre>{@code
 *  prism (0,0,0) (0,10,0) (1,0,0) (0,0,1) - Creates a 3-sided prism (triangle) with radius 5,
 *                                            bottom center at (0,0,0), top center at (0,10,0),
 *                                            and faces aligned with the XZ plane.
 *  p (1,1,1) (1,5,1) (1,0,0) (0,1,0) (1,0,0) (0,1,0) - Creates a prism with custom top and bottom plane definitions.
 *  p plane:"xy" (0, 1, 0) (0, 10, 0)       - Creates a prism who's faces lie on the XY plane using unordered
 *                                          tagged arguments
 *  p (0, 1, 0) (0, 10, 0)                  - Creates a prism who's faces are aligned with the XZ plane.
 *  }</pre>
 * @implNote All plane vectors are implicitly normalised. If you know what you're doing
 * you can apply the {@code unsafe:true} tagged argument into the command input such that
 * all vectors you input (that aren't themselves with a magnitude of 1) don't get normalised.
 *
 * @author Lehlogonolo Poole
 * @see Command
 * @see AxisPlane
 * @see Vector3
 * @see Thing
 * @see KeyedStatefulCommand
 */
public class PrismCmd extends Command implements KeyedStatefulCommand {

    /** An array of step sizes (e.g., 1, 5, 20) that the user can cycle through. */
    protected double[] gearTrain = new double[]{1, 5, 20, 50};
    /** The index of the currently active step size in the gearTrain. */
    protected int currentIndex = 0;
    /** The keybinding used to cycle through the gearTrain step sizes. */
    protected J3Key gear;    /** A list of temporary keybindings (e.g., arrow keys) active during this state. */
    protected ArrayList<J3Key> keys = new ArrayList<>();

    Vector3 bottomFaceCenter, topFaceCenter;
    AxisPlane bottom, top;
    int radius = 5;
    int sides = 3;
    UUID overlapId = UUID.randomUUID();

    public PrismCmd() {
        super("prism", "Create a prism with the given bottom face and top face");
        this.aliases("p").args(
                new TypedArg(
                        "bottomFace", "The bottom face's centre",
                        false, Vector3.class),
                new TypedArg(
                        "topFace", "The top face's centre",
                        false, Vector3.class),
                new TypedArg(
                        "bottomPlanev1", "The first vector of the bottom face's plane",
                        true, Vector3.class
                ),
                new TypedArg(
                        "bottomPlanev2", "The second vector of the bottom face's plane",
                        true, Vector3.class
                ),
                new TypedArg(
                        "topPlanev1", "The first vector of the top face's plane",
                        true, Vector3.class
                ),
                new TypedArg(
                        "topPlanev2", "The second vector of the top face's plane",
                        true, Vector3.class
                )
        ).parseUsages();

        gear = newGearKey("prism");

        setUpKey(
                ()->null,
                n->false,
                o-> {
                    editRadius(true);
                }
        );
        setDownKey(
                ()->null,
                n->false,
                o-> {
                    editRadius(false);
                }
        );
        setLeftKey(
                ()->null,
                n->false,
                o-> {
                    editSides(true);
                }
        );
        setRightKey(
                ()->null,
                n->false,
                o-> {
                    editSides(false);
                }
        );
    }

    private void editRadius(boolean add) {
        if (!add && radius-1 < 1) return;
        if (add) radius += gearTrain[currentIndex];
        else radius -= gearTrain[currentIndex];
    }

    private void editSides(boolean add) {
        if (!add && sides-1 < 3) return;
        if (add) sides += 1;
        else sides -= 1;
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        // do validation and shi
        if (args.length < 2) {
            logLabel.setText("Not enough arguments given. Usage: "
                    + aliasUsed + " "
                    + getUsages().values().stream().findAny().orElse(""));
            CommandsManager.clearCurrent();
            return;
        }
        // check that all args are a Vector3 input
        for (Object arg : args) {
            if (!(arg instanceof Vector3)) {
                logLabel.setText("All arguments must be (vector3) objects. e.g: (0, 9, 1)");
                return;
            }
        }

        bottomFaceCenter = (Vector3) args[0];
        topFaceCenter = (Vector3) args[1];

        // check for plane inputs via tagged args before explicit args.
        TaggedArgValue<String> plane1 =
                TaggedArgUtil.getTaggedArg(taggedArgs, "plane", String.class);

        // If no plane arguments are given at all. Default to XZ plane.
        Vector3 bottomPlaneV1 = new Vector3(1, 0, 0),
                bottomPlaneV2 = new Vector3(0, 0, 1),
        topPlaneV1, topPlaneV2;

        if (plane1 != null) {
            // In this case, any plane input is treated as the top plane,
            // where as the bottom plane is the fixed string from the tagged argument.
            // So the following input converge here to the exact same output:

            // prism (0, 1, 0) (0, 10, 0) plane:"xz" (0, 0.2, 0.4) (0, 0.2, 0.4)
            // prism (0, 1, 0) (0, 10, 0) (0, 0.2, 0.4) (0, 0.2, 0.4) plane="XZ"
            // prism plane:"XZ" (0, 1, 0) (0, 20, 0)
            // (Since tagged arguments aren't ordered.)

            String val = plane1.value.toLowerCase();
            switch (val) {
                case "xz", "zx" -> {
                    bottomPlaneV1 = new Vector3(1, 0, 0);
                    bottomPlaneV2 = new Vector3(0, 0, 1);
                }
                case "yz", "zy" -> {
                    bottomPlaneV1 = new Vector3(0, 1, 0);
                    bottomPlaneV2 = new Vector3(0, 0, 1);
                }
                case "xy", "yx" -> {
                    bottomPlaneV1 = new Vector3(0, 1, 0);
                    bottomPlaneV2 = new Vector3(1, 0, 0);
                }
            }

            topPlaneV1 = (args.length > 3) ? ((Vector3) args[4]) : bottomPlaneV1;
            topPlaneV2 = (args.length > 4) ? ((Vector3) args[5]) : bottomPlaneV2;
        } else {
            // In this case, it's explicit defined.
            bottomPlaneV1 = (args.length > 3) ? ((Vector3) args[3]) : bottomPlaneV1;
            bottomPlaneV2 = (args.length > 4) ? (Vector3) args[4] : bottomPlaneV2;

            topPlaneV1 = (args.length > 4) ? ((Vector3) args[4]) : bottomPlaneV1;
            topPlaneV2 = (args.length > 5) ? ((Vector3) args[5]) : bottomPlaneV2;
        }

        bottom = new AxisPlane(bottomFaceCenter, bottomPlaneV1, bottomPlaneV2);
        top = new AxisPlane(topFaceCenter, topPlaneV1, topPlaneV2);

        // check for unsafe.
        TaggedArgValue<Boolean> unsafe =
                TaggedArgUtil.getTaggedArg(taggedArgs, "unsafe", Boolean.class);

        if (unsafe != null && !unsafe.value) {
            bottom = bottom.normalize();
            top = top.normalize();
        }

        keys.add(gear);
        run(this, "prism", null, logLabel);
    }

    @Override
    public void onStart(Void object, SafeJLabel label) {
        keys.forEach(StaticRefs.getGlobalKeybinds()::registerJ3Key);

        Consumer<Graphics2D> drawGhosts = g -> {
            g.setColor(Color.WHITE);
            // Draw a line connecting the 2 centres
            sceneManager.drawLine3D(
                    g, bottomFaceCenter, topFaceCenter, StaticRefs.getCamera()
            );
            // Draw 2 n-gons on the top and bottom face.
            ArrayList<Vector3> bottomFace = Sampler.ngon(
                    radius,
                    bottom,
                    sides
            );
            Sampler.joinNGonArbitaryVectors(bottomFace, g);
            ArrayList<Vector3> topFace = Sampler.ngon(
                    radius,
                    top,
                    sides
            );
            Sampler.joinNGonArbitaryVectors(topFace, g);

            label.setText(
                    SafeJLabel.EMPH + "-sided prism with radius "+SafeJLabel.EMPH+" using arrow keys and handles. "
                            +"| (Click "+SafeJLabel.EMPH+" to change radius)",
                    sides,
                    new JLabelRichText(""+radius)
                            .font(J3DTheme.TEXT_SECONDARY.color().darker(), "6"),
                    "[R]"
            );
        };

        sceneManager.scheduleOverlap(overlapId, drawGhosts);
    }

    @Override
    public void onEnter(ActionEvent e, Void object, SafeJLabel label) {
        String name = JOptionPane.showInputDialog(
                "What must this be named gng?"
        );
        if (name == null) {
            finish(label);
            label.setText("The thing needs a name my friend.");
            return;
        }
        Layer l = new Layer(name);
        sceneManager.layers.add(l);
        Solids.prism(
                radius,
                sides,
                l,
                new SamePair<>(bottom, top)
        );
        finish(label);
    }

    @Override
    public void onEsc(ActionEvent e, Void object, SafeJLabel label) {
        finish(label);
    }

    private void finish(SafeJLabel label) {
        sides = 3;
        radius = 5;
        sceneManager.removeOverlap(overlapId);
        label.clear();
        keys.forEach(key -> StaticRefs.getGlobalKeybinds().removeJ3Key(key.getId()));
    }

    @Override
    public ArrayList<J3Key> getKeys() {
        return keys;
    }

    @Override
    public String selfName() {
        return "prism";
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
