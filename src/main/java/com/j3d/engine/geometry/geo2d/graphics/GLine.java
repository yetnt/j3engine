package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.Static;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.geometry.constraints.ConstraintManager;
import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo2d.constraints.CLine;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.properties.Property;
import com.j3d.storage.files.ProjectFile;
import com.j3d.ui.dialog.Spinner;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.j3d.Static.camera;
import static com.j3d.Static.sceneManager;

/**
 * GLine represents, you guessed it, a line.
 * <p>
 *     A GLine's pivot is always defined as its midpoint.
 * </p>
 * <p>
 *     A GLine is drawn by a parent {@link GTri} but otherwise is stored
 *     alongside other GObjects within a {@link Thing}. And a GLine also
 *     draws it's relevant {@link GPoint}s
 * </p>
 * @author Lehlogonolo Poole
 * @see Thing
 * @see GPoint
 * @see GTri
 */
public class GLine extends GObject implements HasParents<GTri>, IdempotentEventListener<GPoint.GPointMovedEvent, Vector3> {
    /**
     * The startpoint of this line
     */
    protected GPoint startPoint;
    /**
     * The endPoint of this line.
     */
    protected GPoint endPoint;
    protected boolean deletedState = false;
    private HashSet<GTri> parents = new HashSet<>();
    protected ConstraintManager<GLine> constraints = new ConstraintManager<>();

    /**
     * Constructs a GLine.
     * @implSpec This is used by {@link ProjectFile#readFile(String, String, Spinner)} during a project file read and should only be used in that case.
     * @param id The id of the line defined by the file
     * @param A The first constructed GPoint reference.
     * @param B The second constructed GPoint reference.
     * @return A GLine
     */
    public static GLine fromRaw(String id, GPoint A, GPoint B) {
        GLine gl = new GLine(A, B);
        gl.setId(UUID.fromString(id));
        return gl;
    }

    /**
     * Draws this line to the screen.
     * @implSpec This is only called by {@link GTri#draw(Graphics2D)}
     * @implNote This respects {@link ViewType} and may or may not draw
     * itself depending on the type.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void draw(Graphics2D graphics2D) {
        if (deletedState) return;
        if (sceneManager.getSelected().contains(this)) {
            drawSelected(graphics2D);return;
        }
        graphics2D.setColor(col);
        swingDraw(graphics2D);
        // dispatch to points
        startPoint.draw(graphics2D);
        endPoint.draw(graphics2D);
    }

    public void swingDraw(Graphics2D graphics2D) {
        graphics2D.drawLine(
                startPoint.getPivot().toPoint(camera).toScreen(sceneManager).x,
                startPoint.getPivot().toPoint(camera).toScreen(sceneManager).y,
                endPoint.getPivot().toPoint(camera).toScreen(sceneManager).x,
                endPoint.getPivot().toPoint(camera).toScreen(sceneManager).y
        );
    }

    /**
     * Draws this line to the screen in its selected state.
     * @implSpec This is only called by {@link GTri#drawSelected(Graphics2D)}
     * @implNote This respects {@link ViewType} and may or may not draw
     * itself depending on the type.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void drawSelected(Graphics2D graphics2D) {
        if (deletedState) return;
        graphics2D.setColor(col.brighter());
        graphics2D.setStroke(new BasicStroke(4));
        swingDraw(graphics2D);
        graphics2D.setStroke(new BasicStroke(1));
        // dispatch to points
        startPoint.drawSelected(graphics2D);
        endPoint.drawSelected(graphics2D);
    }

    /**
     * Default Constructor
     *
     * @param A The start point
     * @param B THe end point
     */
    public GLine(GPoint A, GPoint B) {
        startPoint = A;
        endPoint = B;

        // set the pivot to the midpoint of the line
        setPivot(A.getPivot().add(B.getPivot()).div(2));
        toConstraintObject();

        A.attach(this);
        A.addParent(this);
        B.attach(this);
        B.addParent(this);
        Static.sceneManager.hasNoParent(this);
        addProps();
    }

    private void addProps() {
        properties.addAll(List.of(
                new Property<>("Start", () -> startPoint, GLine.class)
                        .setDescription("The start point of this line").constant(),
                new Property<>("End", () -> endPoint, GLine.class)
                        .setDescription("The end point of this line").constant()
        ));
    }

    /**
     * @implNote This also deletes it's child points.
     * @return true if the line was deleted
     */
    @Override
    public boolean deleteSelf() {
        // TODO: If a line is deleted but it was parented, triangles loose integrity (and calling delete on it is unsafe as it refernces this currently deleted line.)
        super.deleteSelf();
        getPointStream().forEach(
                p -> {
                    p.removeParent(this);
                    if (!p.hasParent()) p.deleteSelf();
                }
        );
        return true;
    }

    /**
     * Returns the end GPoint
     * @return GPoint
     */
    public GPoint getEnd() {
        return endPoint;
    }

    /**
     * Returns the start GPoint
     * @return start
     */
    public GPoint getStart() {
        return startPoint;
    }

    /**
     * Calculates and returns the length of this line.
     * @return a double representing the length of the line.
     */
    public double length() {
        return Math.sqrt(
                Math.pow(startPoint.getPivot().getX()- endPoint.getPivot().getX(), 2) + Math.pow(startPoint.getPivot().getY()- endPoint.getPivot().getY(), 2)
        );
    }

    @Override
    public String toString() {
        return "GLine [ " + startPoint +
                " -> " + endPoint +
                " ]";
    }

    /**
     * Creates a stream of this line's points.
     * @return A stream of GPoints
     */
    public Stream<GPoint> getPointStream() {
        return Stream.of(startPoint, endPoint);
    }

    @Override
    public CLine toConstraintObject() {
        if (constraintObject == null) {
            constraintObject = new CLine(this);
        }
        return (CLine) constraintObject;
    }

    @Override
    public ConstraintManager<GLine> getConstraints() {
        return constraints;
    }


    @Override
    public HashSet<GTri> getParents() {
        return parents;
    }

    @Override
    public void addParent(GTri parent) {
        boolean su = parents.add(parent);
        if (su) toConstraintObject().addParent(
                parent.toConstraintObject()
        );
        Static.sceneManager.hasParent(this);
    }

    @Override
    public void removeParent(GTri parent) {
        boolean su = parents.remove(parent);
        if (su) toConstraintObject().removeParent(
                parent.toConstraintObject()
        );
        Static.sceneManager.hasNoParent(this);
    }

    @Override
    public void onEvent(EventType event, EventPayload properties) {
        if (event == EventType.GPOINT_RECALC_PIVOT && properties instanceof GPoint.GPointMovedEvent p)
            handlePossibleDuplicates(event, p);
    }

    @Override
    public Vector3 getDupeObjectToCheck() {
        return getPivot();
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPoint.GPointMovedEvent payload) {
        Vector3 piv = getStart().getPivot().add(getEnd().getPivot()).div(2);
        if (!piv.equals(getDupeObjectToCheck()))  setPivot(piv);
    }

    public ArrayList<GPoint> explode(Thing parent) {
        deletedState = true;
        parent.getObjects().remove(this);
        sceneManager.getUnparented().remove(this);
        ArrayList<GPoint> pointsList = getPointStream().collect(Collectors.toCollection(ArrayList::new));
        pointsList.forEach(point -> point.explode(this));
        startPoint = null;
        endPoint = null;
        return pointsList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GLine gLine = (GLine) o;
        return getId().equals(gLine.getId());
//                deletedState == gLine.deletedState &&
//                Objects.equals(startPoint, gLine.startPoint) &&
//                Objects.equals(endPoint, gLine.endPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
