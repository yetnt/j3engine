package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.constraints.*;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.OtherKeys;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractTransform extends Subcommand implements StatefulCommand<Void> {

    protected UUID overlapId;
    private final TransformMouseOwner mouseOwner;
    protected ArrayList<J3Key> keys = new ArrayList<>();
    protected ArrayList<Vector3> originalPointPos = new ArrayList<>();
    protected ArrayList<GPoint> references = new ArrayList<>();
    protected Vector3 center;
    protected ArgSet argSet =
            new ArgSet(
                    "mode",
                    "What the transformation should operate on",
                    true,
                    "p", "v", // Points/vertices
                    "t", "f" // Triangles/faces
            );
    protected String eventName;
    protected boolean faceMode = true;
    protected double[] gearTrain;
    protected int currentIndex = 0;
    protected J3Key gear = new J3Key(
            "transformGearKey",
            OtherKeys.TRANSFORM_CHANGE_STEP_SIZE.getKeyStroke(),
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentIndex = (currentIndex + 1) % gearTrain.length;
                }
            }
    );
    protected SafeJLabel label;

    public AbstractTransform(String commandName, String commandDesc, String eventName, TransformMouseOwner mouseOwner, double[] gearTrain) {
        super(commandName, commandDesc);
        this.mouseOwner = mouseOwner;
        this.eventName = eventName;
        this.gearTrain = gearTrain;
    }

    public double getCurrentStepSize() {
        return gearTrain[currentIndex];
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        CommandsManager.setAsCurrent(this);
        this.label = logLabel;

        if (args.length > 0 && !(args[0] instanceof String)) {
            Static.log.println("Second argument has to be a string!");
            return;
        }
        keys.add(gear);
        if (args.length > 0 && argSet.isValid((String)args[0])) {
            String arg = (String)args[0];
            faceMode = arg.equals("f") || arg.equals("v");
        }

        Stream<GTri> tris = Static.renderer.getSelected().stream()
                .filter(obj -> obj instanceof GTri)
                .map(obj -> (GTri) obj);

        if (tris.findAny().isEmpty()) faceMode = false;

        // Simple 3 dots
        references =
                faceMode ?
                        new ArrayList<>(Static.renderer.getSelected().stream()
                                .filter(obj -> obj instanceof GTri)
                                .map(obj -> (GTri) obj)
                                .flatMap(GTri::getLegStream)
                                .flatMap(GLine::getPointStream)
                                .collect(Collectors.toSet()))
                        : Static.renderer.getSelected()
                        .stream()
                        .filter(obj -> obj instanceof GPoint)
                        .map(obj -> (GPoint) obj)
                        .collect(Collectors.toCollection(ArrayList::new));

        originalPointPos = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        run(this, eventName, null, logLabel);
    }

    @Override
    public void onStart(Void object, SafeJLabel label) {
        mouseOwner.requestOwnership();

        keys.forEach(
                key -> Static.keybinds.registerJ3Key(key)
        );

        center = Vector3.reduceToVector3(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                , Vector3::add).div(references.size());
        double farPosX = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getX() > v2)
                        return v1.getX();
                    return v2;
                },
                0d
        );
        double farPosY = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getY() > v2)
                        return v1.getY();
                    return v2;
                },
                0d
        );
        double farPosZ = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getZ() > v2)
                        return v1.getZ();
                    return v2;
                },
                0d
        );

        // Draw 3 circles
        // a blue one at x=0, y=4, z=0
        // a red one at x=4, y=0, z=0
        // a green one at x=0, y=0, z=4
        final int size = 10;
        Handle X = new Handle(
                HandleType.X, center.add(new Vector3(10, 0, 0)),
                (gr, p) -> {
                    gr.setColor(Color.RED);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Y = new Handle(
                HandleType.Y, center.add(new Vector3(0, 10, 0)),
                (gr, p) -> {
                    gr.setColor(Color.BLUE);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Z = new Handle(
                HandleType.Z, center.add(new Vector3(0, 0, 10)),
                (gr, p) -> {
                    gr.setColor(Color.GREEN);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });

        mouseOwner.setHandles(new ArrayList<>(List.of(X, Y, Z)), references);
        Consumer<Graphics2D> drawScaleHandle = g -> {
            // this draws the handles such that the user can itneract with it
            // in real time and watch it warp and change.
            center = Vector3.reduceToVector3(
                    references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                    , Vector3::add).div(references.size());
            X.setPos(center.add(new Vector3(10, 0, 0)));
            Y.setPos(center.add(new Vector3(0, 10, 0)));
            Z.setPos(center.add(new Vector3(0, 0, 10)));
            X.draw(g);
            Y.draw(g);
            Z.draw(g);
            g.setColor(Color.WHITE);
            String capitalizedName = getName().replaceFirst(
                    "[a-z]"
                    , String.valueOf(getName().charAt(0)).toUpperCase()
            );
            String stepsTitle = (switch (this) {
                case RotateSelection r -> "Angle";
                case ScaleSelection s -> "Scale";
                case TranslateSelection t -> "Distance";
                default -> throw new IllegalStateException("Unexpected value: " + this);
            });
            label.setText(
                    SafeJLabel.EMPH + " " + SafeJLabel.EMPH + " using arrow keys and handles. | "
                    + SafeJLabel.EMPH + SafeJLabel.EMPH + " (Click "+SafeJLabel.EMPH+" to change)",
                    capitalizedName,
                    new JLabelRichText(faceMode ? "faces" : "points")
                            .font(J3DTheme.TEXT_SECONDARY.color().darker(), "6"),
                    stepsTitle + ": ",
                    new JLabelRichText(Double.toString(getCurrentStepSize()) +
                            (this instanceof ScaleSelection s ? "/" + Double.toString(s.getInverseStepSize()) : "") +
                            (this instanceof RotateSelection ? '°' : " units")
                    )
                            .font(J3DTheme.TEXT_SECONDARY.color().brighter(), "6"),
                    "[R]"
            );
        };

        overlapId = UUID.randomUUID();

        Static.renderer.scheduleOverlap(overlapId, drawScaleHandle);
    }

    private void finished(SafeJLabel lbl) {
        keys.forEach(
                key -> Static.keybinds.removeJ3Key(key.getId())
        );
        Static.renderer.removeOverlap(overlapId);
        lbl.clear();
        Static.renderer.deselectAll();
        Static.mainFrame.repaint();
    }


    @Override
    public void onEnter(ActionEvent e, Void object, SafeJLabel label) {
        // later wrap as Action for the final ransform applied.
        EngineFrame.setMouseOwner(null);
        ArrayList<Vector3> newPositions = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        Renderer.history.add(
                new VoidAction() {
                    @Override
                    public Void run() {
                        references.forEach(p -> p.setPivot(newPositions.get(references.indexOf(p))));
                        return null;
                    }

                    @Override
                    public void undo() {
                        references.forEach(p -> p.setPivot(originalPointPos.get(references.indexOf(p))));
                    }

                    @Override
                    public boolean isReversible() {
                        return true;
                    }

                    @Override
                    public String getDescription() {
                        return "TransformSelection:"+getName();
                    }
                }
        );
        finished(label);
    }

    @Override
    public void onEsc(ActionEvent e, Void object, SafeJLabel label) {
        // clear transforms done.
        EngineFrame.setMouseOwner(null);
        for (GPoint p : references) p.setPivot(originalPointPos.get(references.indexOf(p)));
        finished(label);
    }

    public static String UP = "upArrowKey";
    public static String DOWN = "downArrowKey";
    public static String LEFT = "leftArrowKey";
    public static String RIGHT = "rightArrowKey";

    /**
     * Configures and registers a {@link J3Key} for a specific transformation direction (e.g., Up, Down, Left, Right).
     * This method encapsulates the complex logic of applying a transformation while
     * integrating the constraint validation system.
     *
     * @param key        A string identifier for the key (e.g., {@link #UP}, {@link #DOWN}).
     * @param lbl        A {@link SafeJLabel} used for providing user feedback, especially
     *                   when a proposed transformation fails due to a constraint violation.
     * @param earlyExit  If this function returns true, the action is aborted.
     * @param shared     A {@link Supplier} that provides a shared variable (of type {@code T})
     *                   representing the magnitude or specific value of the transformation. This
     *                   value is used by both the constraint checker and the actual application
     *                   of the transform.
     * @param application A {@link Consumer} that applies the transformation to the actual
     *                    {@link GObject}s. This consumer is only executed if all constraints are satisfied.
     * @param biConsumer  A {@link BiConsumer} that applies the transformation to the
     *                    {@link ConstraintMirror} objects within a {@link ConstraintIntent}.
     *                    This is the "what-if" function used by the constraint system to check
     *                    if the proposed transform breaks any rules without modifying the original objects.
     * @param <T>        The type of the shared variable representing the transformation value.
     * @implSpec This method is not intended for direct use. Instead, specialized setter methods
     *           (e.g., {@code setUpKey()}, {@code setDownKey()}) should be implemented in
     *           subclasses, making use of the static key constants defined in {@link AbstractTransform}.
     */
    protected <T> void setKey(String key, Supplier<SafeJLabel> lbl, Function<T, Boolean> earlyExit, Supplier<T> shared, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        keys.add(
                new J3Key(
                        key + getName(),
                        KeyStroke.getKeyStroke(
                                switch (key) {
                                    case "upArrowKey" -> KeyEvent.VK_UP;
                                    case "downArrowKey" -> KeyEvent.VK_DOWN;
                                    case "leftArrowKey" -> KeyEvent.VK_LEFT;
                                    case "rightArrowKey" -> KeyEvent.VK_RIGHT;
                                    default -> throw new IllegalStateException("Unexpected value: " + key);
                                }
                                , 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                T sharedVar = shared.get();
                                if (earlyExit.apply(sharedVar)) return;
                                ArrayList<ConstraintMirror> c = ConstraintUtils.converter(
                                        references.stream().map(o -> (GObject)o).collect(Collectors.toCollection(ArrayList::new))
                                );
                                ConstraintIntent intent = new ConstraintIntent(c,
                                        (mp) -> biConsumer.accept(sharedVar, mp)
                                );
                                for (GPoint ref : references) {
                                    boolean allConstr = ref.getConstraints().allSatisfied(
                                            lbl.get(),
                                            "Cannot transform object due to " + SafeJLabel.EMPH,
                                            intent
                                    );
                                    if (!allConstr) return; // method above sent user UX
                                }
                                // if we make it here, apply evberything as normal.
                                application.accept(sharedVar);
                                references.stream()
                                        .map(GPoint::getConstraints)
                                        .flatMap(ConstraintManager::constraintStream)
                                        .forEach(ConstraintOn::applyConstraint);
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );
    }

    public Supplier<SafeJLabel> getLabel() {
        return () -> label;
    }

    protected <T> void setUpKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(UP, getLabel(), earlyExit, shared, application, biConsumer);
    }
    protected <T> void setDownKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(DOWN, getLabel(), earlyExit, shared, application, biConsumer);
    }
    protected <T> void setLeftKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(LEFT, getLabel(), earlyExit, shared, application, biConsumer);
    }
    protected <T> void setRightKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(RIGHT, getLabel(), earlyExit, shared, application, biConsumer);
    }
}
