package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.StaticRefs;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.properties.Property;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.dialog.Spinner;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.j3d.StaticRefs.camera;
import static com.j3d.StaticRefs.sceneManager;

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
    protected GPoint pointA;
    /**
     * The endPoint of this line.
     */
    protected GPoint pointB;
    protected boolean deletedState = false;
    private HashSet<GTri> parents = new HashSet<>();

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
        super.draw(graphics2D);
        if (deletedState) return;
        if (sceneManager.getSelected().contains(this)) {
            drawSelected(graphics2D);return;
        }
        graphics2D.setColor(col);
        swingDraw(graphics2D);
        // dispatch to points
        pointA.draw(graphics2D);
        pointB.draw(graphics2D);
    }

    public void swingDraw(Graphics2D graphics2D) {
        graphics2D.drawLine(
                pointA.getPivot().toPoint(camera).toScreen(sceneManager).x,
                pointA.getPivot().toPoint(camera).toScreen(sceneManager).y,
                pointB.getPivot().toPoint(camera).toScreen(sceneManager).x,
                pointB.getPivot().toPoint(camera).toScreen(sceneManager).y
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
        super.drawSelected(graphics2D);
        if (deletedState) return;
        graphics2D.setColor(col.brighter());
        graphics2D.setStroke(new BasicStroke(4));
        swingDraw(graphics2D);
        graphics2D.setStroke(new BasicStroke(1));
        // dispatch to points
        pointA.drawSelected(graphics2D);
        pointB.drawSelected(graphics2D);
    }

    /**
     * Default Constructor
     *
     * @param A The start point
     * @param B THe end point
     */
    public GLine(GPoint A, GPoint B) {
        pointA = A;
        pointB = B;

        // set the pivot to the midpoint of the line
        setPivot(A.getPivot().add(B.getPivot()).div(2));

        A.attach(this);
        A.addParent(this);
        B.attach(this);
        B.addParent(this);

        StaticRefs.sceneManager.hasNoParent(this);
        A.addParent(this);
        B.attach(this);
        addProps();
    }

    private void addProps() {
        properties.addAll(List.of(
                new Property<>("Point A", this::getA, GLine.class)
                        .holds(GPoint.class)
                        .setDescription("The start point of this line").constant(),
                new Property<>("Point B", this::getB, GLine.class)
                        .holds(GPoint.class)
                        .setDescription("The end point of this line").constant()
        ));
        pivotProperty.constant(); // the pivot cannot be edited.
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
    public GPoint getB() {
        return pointB;
    }

    /**
     * Returns the start GPoint
     * @return start
     */
    public GPoint getA() {
        return pointA;
    }

    /**
     * Calculates and returns the length of this line.
     * @return a double representing the length of the line.
     */
    public double length() {
        return Math.sqrt(
                Math.pow(pointA.getPivot().getX()- pointB.getPivot().getX(), 2) + Math.pow(pointA.getPivot().getY()- pointB.getPivot().getY(), 2)
        );
    }

    @Override
    public String toString() {
        return "GLine [ " + pointA +
                " -> " + pointB +
                " ]";
    }

    /**
     * Creates a stream of this line's points.
     * @return A stream of GPoints
     */
    public Stream<GPoint> getPointStream() {
        return Stream.of(pointA, pointB);
    }


    @Override
    public HashSet<GTri> getParents() {
        return parents;
    }

    @Override
    public void addParent(GTri parent) {
        parents.add(parent);
        StaticRefs.sceneManager.hasParent(this);
    }

    @Override
    public void removeParent(GTri parent) {
        boolean su = parents.remove(parent);
        StaticRefs.sceneManager.hasNoParent(this);
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
        Vector3 piv = getA().getPivot().add(getB().getPivot()).div(2);
        if (!piv.equals(getDupeObjectToCheck()))  setPivot(piv);
    }

    public ArrayList<GPoint> explode(Thing parent) {
        deletedState = true;
        parent.getObjects().remove(this);
        ArrayList<GPoint> pointsList = getPointStream().collect(Collectors.toCollection(ArrayList::new));
        pointsList.forEach(point -> {
            if (point != null) {
                point.explode(this);
                detach(point);
                point.detach(this);
            }
        });
        sceneManager.hasParent(this);
        pointA = null;
        pointB = null;
        return pointsList;
    }

    /**
     * Checks if this GLine's points are identical to the given points, regardless of order.
     * @param A The first GPoint to compare.
     * @param B The second GPoint to compare.
     * @return true if the points match, false otherwise.
     */
    public boolean identicalPoints(GPoint A, GPoint B) {
        return (pointA.equals(A) && pointB.equals(B))
                ||
                (pointA.equals(B) && pointB.equals(A));
    }

    /**
     * Retrieves an existing GLine from a list if its points match the given points, or creates a new GLine if no match is found.
     * @param lines The list of existing GLines to search through.
     * @param A The first GPoint for comparison or new GLine creation.
     * @param B The second GPoint for comparison or new GLine creation.
     * @return An existing GLine with matching points, or a new GLine if none exists.
     */
    public static GLine getInstance(HashSet<GLine> lines, GPoint A, GPoint B) {
        if (lines.isEmpty()) return new GLine(A, B);

        return lines.stream()
                .filter(line -> line.identicalPoints(A, B))
                .findAny()
                .orElseGet(() -> new GLine(A, B));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GLine gLine = (GLine) o;
        return getId().equals(gLine.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
