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
import com.j3d.storage.files.ProjectFile;
import com.j3d.ui.util.Throbber;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Stream;

import static com.j3d.Static.camera;
import static com.j3d.Static.renderer;

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
    private final GPoint startPoint;
    /**
     * The endPoint of this line.
     */
    private final GPoint endPoint;
    private HashSet<GTri> parents = new HashSet<>();
    protected ConstraintManager<GLine> constraints;

    /**
     * Constructs a GLine.
     * @implSpec This is used by {@link ProjectFile#readFile(String, String, Throbber)} during a project file read and should only be used in that case.
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
        graphics2D.setColor(col);
        graphics2D.drawLine(
                startPoint.getPivot().toPoint(camera).toScreen(renderer).x,
                startPoint.getPivot().toPoint(camera).toScreen(renderer).y,
                endPoint.getPivot().toPoint(camera).toScreen(renderer).x,
                endPoint.getPivot().toPoint(camera).toScreen(renderer).y
        );
        // dispatch to points
        startPoint.draw(graphics2D);
        endPoint.draw(graphics2D);
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
        graphics2D.setColor(col.brighter());
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawLine(
                startPoint.getPivot().toPoint(camera).toScreen(renderer).x,
                startPoint.getPivot().toPoint(camera).toScreen(renderer).y,
                endPoint.getPivot().toPoint(camera).toScreen(renderer).x,
                endPoint.getPivot().toPoint(camera).toScreen(renderer).y
        );
        graphics2D.setStroke(new BasicStroke(1));
        draw(graphics2D);
        // dispatch to points
        startPoint.drawSelected(graphics2D);
        endPoint.drawSelected(graphics2D);
//        renderer.drawText3D(graphics2D, getPivot().sub(new Vector3(1, 1, 1)), "[{" + getPivot().getY() + ", " + getPivot().getX() + ", " + getPivot().getZ() + "} -> {" +
//                 endPoint.getPivot().getY() + ", " + endPoint.getPivot().getX() + ", " + endPoint.getPivot().getZ() +
//                "}]", camera);
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
        B.attach(this);
        Static.renderer.hasNoParent(this);
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
        Static.renderer.hasParent(this);
    }

    @Override
    public void removeParent(GTri parent) {
        boolean su = parents.remove(parent);
        if (su) toConstraintObject().removeParent(
                parent.toConstraintObject()
        );
        Static.renderer.hasNoParent(this);
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
}
