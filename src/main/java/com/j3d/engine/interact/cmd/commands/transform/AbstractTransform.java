package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.*;
import com.j3d.engine.interact.cmd.commands.transform.mouse.ChangeCentreEventPayload;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.StaticConfig;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.commands.transform.handles.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handles.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.generators.JLabelRichText;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An abstract base class that provides the core framework for all stateful transformation
 * commands (Translate, Rotate, Scale).
 * <p>
 * This class implements the {@link KeyedStatefulCommand} interface and handles the
 * complex, shared logic required to enter a "transform mode." This includes:
 * <ul>
 *     <li>Collecting the selected objects (references) to be transformed.</li>
 *     <li>Storing their original positions for undo/cancel functionality.</li>
 *     <li>Spawning and managing the 3D interactive handles (gizmos).</li>
 *     <li>Registering and unregistering temporary keybindings (arrow keys, gear key).</li>
 *     <li>Drawing the UI overlay with command-specific information.</li>
 *     <li>Handling the final commit (Enter) or cancellation (Escape) of the transformation.</li>
 * </ul>
 * Subclasses like {@link TranslateSelection} extend this class to define the specific
 * actions that happen when the user interacts with the handles or presses the arrow keys.
 * <p>
 *     Any command implementing this will inherit the need to take in a single optional
 *     {@link ArgSet} called {@code mode} ({@code "f"}, {@code "t"}, {@code "v"}, {@code "p"}, {@code "thing}).
 *     By default, is set to face-mode ({@code "f"} or {@code "t"}). The difference being within
 *     face-mode, the command will filter the selection only for {@link GTri} and take the points of
 *     each triangle as the points to transform. meaning if you had selected only a single point who
 *     is parented to some triangle, all the points in the triangle are part of the transformation
 *     selection as they share the same triangle up to hierarchy. To avoid this, use point-mode
 *     ({@code "p"}, {@code "v"}). Otherwise, if you had selected the {@code "thing"} option
 *     the command will filter the selection only for that {@link Thing} and take all the points of
 *     each thing as the points to transform.
 * </p>
 *
 * @author Lehlogonolo Poole
 * @see KeyedStatefulCommand
 * @see Subcommand
 * @see TransformMouseOwner
 * @see TransformCmd
 * @see TranslateSelection
 * @see ScaleSelection
 * @see RotateSelection
 */
public abstract class AbstractTransform extends Subcommand implements KeyedStatefulCommand, EventListener {

    /** A unique ID for the UI overlay drawn by the sceneManager during the transform state. */
    protected UUID overlapId;
    /** The mouse owner responsible for handling direct interaction with the 3D handles. */
    private final TransformMouseOwner mouseOwner;
    /** A list of temporary keybindings (e.g., arrow keys) active during this state. */
    protected ArrayList<J3Key> keys = new ArrayList<>();
    /** A snapshot of the original positions of all points being transformed, for undo purposes. */
    protected ArrayList<Vector3> originalPointPos = new ArrayList<>();
    /** The list of actual {@link GPoint} objects that are being manipulated. */
    protected ArrayList<GPoint> references = new ArrayList<>();
    /** The calculated center of the current selection. */
    protected Vector3 center;
    /** The argument set for choosing between point/face selection mode. */
    protected ArgSet argSet =
            new ArgSet(
                    "mode",
                    "What the transformation should operate on",
                    true,
                    "p", "v", // Points/vertices
                    "t", "f", // Triangles/faces
                    "thing"
            );
    /** The name of the event associated with this stateful command. */
    protected String eventName;
    /** A flag indicating if the transformation should operate on whole faces (tris) or individual points or whole things. */
    protected FaceMode faceMode = FaceMode.TRIANGLES;
    /** An array of step sizes (e.g., 1, 5, 20) that the user can cycle through. */
    protected double[] gearTrain;
    /** The index of the currently active step size in the gearTrain. */
    protected int currentIndex = 0;
    /** The keybinding used to cycle through the gearTrain step sizes. */
    protected J3Key gear;
    /** The label used for providing feedback to the user. */
    protected SafeJLabel label;

    private boolean centreIsSelectionPivot = true;
    private GPoint selectionPivot = null;

    /**
     * Constructs the abstract base for a transformation command.
     *
     * @param commandName The primary name of the command (e.g., "translate").
     * @param commandDesc A user-friendly description of the command.
     * @param eventName   The name of the stateful event to run.
     * @param mouseOwner  The specific mouse owner that will handle handle interaction.
     * @param gearTrain   An array of step sizes for the command's "gear" system.
     */
    public AbstractTransform(String commandName, String commandDesc, String eventName, TransformMouseOwner mouseOwner, double[] gearTrain) {
        super(commandName, commandDesc);
        this.mouseOwner = mouseOwner;
        this.eventName = eventName;
        this.gearTrain = gearTrain;
        this.gear = newGearKey(commandName);
        keys.add(gear);
        mouseOwner.attach(this);
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.TRANSFORM_CHANGE_CENTRE && properties instanceof ChangeCentreEventPayload cce) {
            // sort closest to the camera
            ArrayList<GPoint> points = new ArrayList<>();
            ScreenPoint target = cce.getMousePos();
            references
                    .stream()
                    .sorted(
                            (a, b) -> {
                                Vector3 aPos = a.getPivot();
                                Vector3 bPos = b.getPivot();
                                return Double.compare(
                                        aPos.distance(StaticRefs.camera.getPosition()),
                                        bPos.distance(StaticRefs.camera.getPosition())
                                );
                            }
                    )
                    .filter(
                            a -> {
                                ScreenPoint Asp = a.getPivot().toPoint(StaticRefs.camera).toScreen(StaticRefs.sceneManager);
                                // a matching points is within 5 up down left or right of the target
                                return Math.abs(Asp.x - target.x) < 5 && Math.abs(Asp.y - target.y) < 5;
                            }
                    )
                    .forEach(points::add);
            if (points.isEmpty()) {
                centreIsSelectionPivot = true;
                selectionPivot = null;
            } else {
                centreIsSelectionPivot = false;
                selectionPivot = points.getFirst();
            }
        }
    }

    /**
     * Gets the currently active step size from the gear train.
     * @return The current step size.
     */
    public double getCurrentStepSize() {
        return gearTrain[currentIndex];
    }

    /**
     * The main entry point for the command. It gathers the selected objects and
     * initiates the stateful transformation mode.
     */
    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        this.label = logLabel;

        if (args.length > 0 && !(args[0] instanceof String)) {
            logLabel.setText("Second argument has to be a string! [p|v|f|t|thing]");
            return;
        }

        CommandsManager.setAsCurrent(this);
        if (args.length > 0 && argSet.isValid((String)args[0])) {
            String arg = (String)args[0];
            faceMode = switch (arg) {
                case "f", "t" -> FaceMode.TRIANGLES;
                case "p", "v" -> FaceMode.POINTS;
                case "thing" -> FaceMode.THING;
                default -> throw new IllegalStateException("Unexpected value: " + arg);
            };
        }

        Stream<GTri> tris = StaticRefs.sceneManager.getSelected().stream()
                .filter(obj -> obj instanceof GTri)
                .map(obj -> (GTri) obj);

        if (tris.findAny().isEmpty()) faceMode = FaceMode.POINTS;

        references = switch (faceMode) {
            case TRIANGLES -> new ArrayList<>(StaticRefs.sceneManager.getSelected().stream()
                    .filter(obj -> obj instanceof GTri)
                    .map(obj -> (GTri) obj)
                    .flatMap(GTri::getLegStream)
                    .flatMap(GLine::getPointStream)
                    .collect(Collectors.toSet()));
            case POINTS -> StaticRefs.sceneManager.getSelected()
                    .stream()
                    .filter(obj -> obj instanceof GPoint)
                    .map(obj -> (GPoint) obj)
                    .collect(Collectors.toCollection(ArrayList::new));
            case THING ->  StaticRefs.sceneManager.getSelected()
                    .stream()
                    .map(g -> StaticRefs.sceneManager.findParentThing(g))
                    .filter(Objects::nonNull)
                    .flatMap(thing -> thing.getObjects().stream())
                    .filter(obj -> obj instanceof GPoint)
                    .map(obj -> (GPoint) obj)
                    .collect(Collectors.toCollection(ArrayList::new));

        };

        originalPointPos = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        run(this, eventName, null, logLabel);
    }

    /**
     * Called when the transformation state begins. This method sets up the interactive environment.
     */
    @Override
    public void onStart(Void object, SafeJLabel label) {
        mouseOwner.requestOwnership();
        keys.forEach(StaticRefs.keybinds::registerJ3Key);

        center = calculateCentre();

        // Create and configure the X, Y, Z handles
        final int size = Settings.editorProperties.handleSize.getValue();
        final double dist = Settings.editorProperties.handleDist.getValue();
        Handle X = new Handle(
                HandleType.X, center.add(new Vector3(dist, 0, 0)),
                (gr, p) -> {
                    gr.setColor(Color.RED);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Y = new Handle(
                HandleType.Y, center.add(new Vector3(0, dist, 0)),
                (gr, p) -> {
                    gr.setColor(Color.BLUE);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Z = new Handle(
                HandleType.Z, center.add(new Vector3(0, 0, dist)),
                (gr, p) -> {
                    gr.setColor(Color.GREEN);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });

        mouseOwner.setHandles(new ArrayList<>(List.of(X, Y, Z)), references);

        // Define the drawing logic for the UI overlay
        Consumer<Graphics2D> drawScaleHandle = g -> {
            final double dist2 = Settings.editorProperties.handleDist.getValue();
            center = calculateCentre();
            X.setPos(center.add(new Vector3(dist2, 0, 0)));
            Y.setPos(center.add(new Vector3(0, dist2, 0)));
            Z.setPos(center.add(new Vector3(0, 0, dist2)));
            X.draw(g);
            Y.draw(g);
            Z.draw(g);
            g.setColor(Color.WHITE);
            String capitalizedName = getName().replaceFirst(
                    "[a-z]"
                    , String.valueOf(getName().charAt(0)).toUpperCase()
            );
            String stepsTitle = (switch (this) {
                case RotateSelection ignored -> "Angle";
                case ScaleSelection ignored -> "Scale";
                case TranslateSelection ignored -> "Distance";
                default -> throw new IllegalStateException("Unexpected value: " + this);
            });
            label.setText(
                    SafeJLabel.EMPH + " " + SafeJLabel.EMPH + " using arrow keys and handles. | "
                            + SafeJLabel.EMPH + SafeJLabel.EMPH + " (Click "+SafeJLabel.EMPH+" to change)",
                    capitalizedName,
                    new JLabelRichText(switch (faceMode) {
                        case THING -> "things";
                        case POINTS -> "points";
                        case TRIANGLES -> "triangles";
                    })
                            .font(J3DTheme.TEXT_SECONDARY.color().darker(), "6"),
                    stepsTitle + ": ",
                    new JLabelRichText(getCurrentStepSize() +
                            (this instanceof ScaleSelection s ? "/" + Double.toString(s.getInverseStepSize()) : "") +
                            (this instanceof RotateSelection ? '°' : " units")
                    )
                            .font(J3DTheme.TEXT_SECONDARY.color().brighter(), "6"),
                    "[R]"
            );

            Stroke s = g.getStroke();

            // draw X at the centre.
            g.setColor(new Color(148, 0, 0));
            g.setStroke(new BasicStroke(3));
            ScreenPoint sp = center.toPoint(StaticRefs.camera).toScreen(StaticRefs.sceneManager);
            int crossSize = 10; // Size of the 'X' cross
            g.drawLine(sp.x - crossSize, sp.y - crossSize, sp.x + crossSize, sp.y + crossSize);
            g.drawLine(sp.x + crossSize, sp.y - crossSize, sp.x - crossSize, sp.y + crossSize);
            g.setColor(Color.WHITE);
            g.setStroke(s);

            mouseOwner.square(g, 5);
        };

        overlapId = UUID.randomUUID();

        StaticRefs.sceneManager.scheduleOverlap(overlapId, drawScaleHandle);
    }

    private Vector3 calculateCentre() {
        if (centreIsSelectionPivot || selectionPivot == null) {
            return Vector3.reduceToVector3(
                    references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                    , Vector3::add).div(references.size());
        } else {
            return selectionPivot.getPivot();
        }
    }

    /**
     * Cleans up the stateful command environment after it has finished.
     */
    private void finished(SafeJLabel lbl) {
        mouseOwner.handles.forEach(Handle::clear);
        selectionPivot = null;
        centreIsSelectionPivot = true;
        keys.forEach(key -> StaticRefs.keybinds.removeJ3Key(key.getId()));
        StaticRefs.sceneManager.removeOverlap(overlapId);
        lbl.clear();
        StaticRefs.sceneManager.deselectAll();
        StaticRefs.mainFrame.repaint();
    }

    /**
     * Called when the user presses Enter, committing the transformation.
     */
    @Override
    public void onEnter(ActionEvent e, Void object, SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        toggleSaved();
        ArrayList<Vector3> newPositions = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        // Add the transformation to the undo/redo history
        SceneManager.history.add(
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
                    public boolean isReversible() { return true; }
                    @Override
                    public String getDescription() { return "TransformSelection:" + getName(); }

                    private final LocalTime now = LocalTime.now();
                    @Override
                    public LocalTime getTime() {
                        return now;
                    }
                }
        );
        finished(label);
    }

    private void toggleSaved() {
        StaticConfig.hasSaved = false;
    }

    /**
     * Called when the user presses Escape, canceling the transformation and reverting all changes.
     */
    @Override
    public void onEsc(ActionEvent e, Void object, SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        for (GPoint p : references) p.setPivot(originalPointPos.get(references.indexOf(p)));
        finished(label);
    }

    // Implementation of KeyedStatefulCommand interface methods
    @Override
    public ArrayList<J3Key> getKeys() { return keys; }
    @Override
    public String selfName() { return getName(); }
    /**
     * Retrieves the initial positions of all points being transformed.
     * This is crucial for undoing the operation if the user cancels.
     * @return An {@link ArrayList} of the original {@link Vector3} positions.
     */
    public ArrayList<Vector3> getOriginalPointPositions() { return originalPointPos; }

    /**
     * Retrieves the actual {@link GPoint} objects that are being manipulated.
     * @return An {@link ArrayList} of the point references.
     */
    public ArrayList<GPoint> getReferences() { return references; }
    @Override
    public double[] getGearTrain() { return gearTrain; }
    @Override
    public int getGearIndex() { return currentIndex; }
    @Override
    public void setGearIndex(int index) { currentIndex = index; }
}
