package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.geometry.constraints.ConstraintManager;
import com.j3d.engine.geometry.geo2d.constraints.CTri;
import com.j3d.engine.geometry.geo3d.Thing;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.storage.files.ProjectFile;
import com.j3d.ui.util.Throbber;

/**
 * GTri represents a Triangle. What'd you expect kau.
 * <p>
 *     A GTri's pivot is set to it's geometric centre between the 3 points.
 * </p>
 * <p>
 *     Unlike the other GObjects, a GTri is a bit more complicated in that
 *     it can be defined in terms of its points or its lines. It is also
 *     where most of the "complex" engine stuff are done like depth calculations
 *     or normal calculations.
 * </p>
 * <p>
 *     A GTri, is stored by reference like any other GObject within a {@link Thing}.
 *     it is also stored within {@link TriStateArea} for draw ordering.
 * </p>
 * @implSpec If defined by the lines, the lines need to connect to each other
 * in order or else calculations will be off.
 * @author Lehlogonolo Poole
 * @see TriStateArea
 * @see Thing
 * @see GPoint
 * @see GLine
 */
public class GTri extends GObject implements IdempotentEventListener<GPoint.GPointMovedEvent, Vector3> {
    /**
     * Leg A, connected to Leg B and Leg C
     */
    private final GLine LegA;
    /**
     * Leg B, connected to Leg A and Leg C
     */
    private final GLine LegB;
    /**
     * Leg C, connected to Leg A and Leg B
     */
    private final GLine LegC;

    /**
     * The normal of the triangle.
     */
    public Vector3 normal;

    // TODO: I actually have no clue where the fuck this is used?? Uhm find this out??
    private boolean hidden = false;
    protected ConstraintManager<GTri> constraints;

    /**
     * Constructs a GTri.
     * @implSpec This is used by {@link ProjectFile#readFile(String, String, Throbber)} during a project file read and should only be used in that case.
     * @param id The id of the triangle defined by the file
     * @param col The colour of the triangle defined by the file
     * @param legA The constructed reference of the first leg
     * @param legB The constructed reference of the second leg
     * @param legC The constructed reference of the third leg
     * @return A GTri
     */
    public static GTri fromRaw(String id, Color col, GLine legA, GLine legB, GLine legC) {
        GTri gt = new GTri(col, legA, legB, legC);
        gt.setId(UUID.fromString(id));
        return gt;
    }

    /**
     * Draws this triangle to the screen.
     * @implSpec This is only called by {@link TriStateArea#draw(Graphics2D)}
     * @implNote This respects {@link ViewType} and may or may not draw
     * itself depending on the type.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void draw(Graphics2D graphics2D) {
        setPivot(LegA.getStart().getPivot().add(LegB.getStart().getPivot()).add(LegC.getStart().getPivot()).div(3));
        calcNormal(LegA.getStart().getPivot(), LegB.getStart().getPivot(), LegC.getStart().getPivot());
        if (J3DSettings.getViewType() == ViewType.NORMAL) {
            graphics2D.setColor(col);
            graphics2D.fillPolygon(new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x
                    },
                    new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y
                    },
                    3
            );
        }
        // dispatch to lines
        LegA.draw(graphics2D);
        LegB.draw(graphics2D);
        LegC.draw(graphics2D);
    }

    /**
     * Private method to draw the triangle's distance depth and normal overlays.
     */
    private void drawDist() {
                Static.renderer.scheduleOverlap(getId(), g -> {
                            if (J3DSettings.isShowTriDistances()) {
                                // draw text showing the tris distance from camera
                                Vector3 triCentroid = this.getPivot();
                                Static.renderer.drawText3D(g, triCentroid,
                                        String.format("Dist: %.2f", this.getPivot().sub(Static.camera.getPosition()).magnitude()),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                            }
                            if (J3DSettings.isShowDepth()) {
                                // draw text showing the tris depth from camera
                                Vector3 triCentroid = this.getPivot();
                                double depth = this.calcDepth();
                                Static.renderer.drawText3D(g, triCentroid.add(new Vector3(1, 0, 0)),
                                        String.format("Depth: %.2f", depth),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                            }
                            if (J3DSettings.isShowNormals()) {
                                // draw text showing the tris normal
                                Vector3 triCentroid = this.getPivot();
                                Static.renderer.drawText3D(g, triCentroid.sub(new Vector3(4, 0, 0)),
                                        String.format("Normal: (%.2f, %.2f, %.2f)", normal.getX(), normal.getY(), normal.getZ()),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                                // The following code draws the normal
                                g.setColor(Color.RED);
                                Static.renderer.drawLine3D(g, getPivot(), getPivot().add(normal.mult(0.5)), Static.camera);
                            }
                        }
                );
    }


    /**
     * Calculates the depth of the tri relative to the camera's forward direction.
     * @return The depth value.
     */
    public double calcDepth() {
        Vector3 toTri = getPivot().sub(Static.camera.getPosition());
        return toTri.dot(Static.camera.getForward().normalize());
    }

    /**
     * Calculates the Euclidean distance from the triangle's pivot to the camera position.
     * @return The Euclidean distance.
     */
    public double euclideanDist() {
        return getPivot().sub(Static.camera.getPosition()).magnitude();
    }

    /**
     * Draws this triangle to the screen in its selected state.
     * @implSpec This is only called by {@link TriStateArea#draw(Graphics2D)}
     * @implNote This respects {@link ViewType} and may or may not draw
     * itself depending on the type.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void drawSelected(Graphics2D graphics2D) {
        setPivot(LegA.getStart().getPivot().add(LegB.getStart().getPivot()).add(LegC.getStart().getPivot()).div(3));
        calcNormal(LegA.getStart().getPivot(), LegB.getStart().getPivot(), LegC.getStart().getPivot());
        if (J3DSettings.getViewType() == ViewType.NORMAL) {
            graphics2D.setColor(col.brighter());
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.fillPolygon(new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x
                    },
                    new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y
                    },
                    3
            );
            graphics2D.setStroke(new BasicStroke(1));
            draw(graphics2D);
            // dispatch to lines
        }
        LegA.drawSelected(graphics2D);
        LegB.drawSelected(graphics2D);
        LegC.drawSelected(graphics2D);
        Static.renderer.drawText3D(graphics2D, getPivot().sub(new Vector3(1, 1, 1)), "Triangle - " + getId(), Static.camera);
    }

    /**
     * Constructs a new GTri from 3 points.
     *
     * @param c The colour
     * @param A Point A
     * @param B Point B
     * @param C Point C
     */
    public GTri(Color c, GPoint A, GPoint B, GPoint C) {
        super(c);

        LegA = new GLine(A, B);
        LegB = new GLine(B, C);
        LegC = new GLine(C, A);

        LegA.addParent(this);
        LegB.addParent(this);
        LegC.addParent(this);

        A.addParents(LegC, LegA);
        B.addParents(LegA, LegB);
        C.addParents(LegB, LegC);

        A.attach(this);
        B.attach(this);
        C.attach(this);

        setPivot(A.getPivot().add(B.getPivot()).add(C.getPivot()).div(3));

        calcNormal(A.getPivot(), B.getPivot(), C.getPivot());

        TriStateArea.register(this);
        drawDist();
        toConstraintObject();
    }

    /**
     * Constructs a new GTri from 3 lines.
     * @implSpec The lines have to connect in a counterclockwise order or clockwise order.
     * In that, 2 lines cannot have the same start point or end points.
     * @param c The colour.
     * @param A The first line.
     * @param B The second line.
     * @param C The third line.
     */
    public GTri(Color c, GLine A, GLine B, GLine C) {
        super(c);
        GPoint[] points = {
                A.getStart(), A.getEnd(),
                B.getStart(), B.getEnd(),
                C.getStart(), C.getEnd()
        };

        // Count how many times each unique point appears
        Map<Vector3, Integer> pointCount = new HashMap<>();
        for (Vector3 p : Arrays.stream(points).map(GObject::getPivot).toList()) pointCount.merge(p, 1, Integer::sum);

        // A valid triangle should have exactly 3 unique points, each appearing twice
        if (pointCount.size() != 3 || pointCount.values().stream().anyMatch(count -> count != 2)) {
            throw new IllegalArgumentException("Lines do not form a closed triangle.");
        }

        // The points have to appear in sequential order. In that the start of a line cannot share another start
        // of another line in a triangle.
        GPoint[] starts = {points[0], points[2], points[4]};
        if (Arrays.stream(starts).collect(Collectors.toSet()).size() < 3)
            throw new IllegalArgumentException("Lines aren't sequential");

        LegA = A;
        LegB = B;
        LegC = C;

        LegA.addParent(this);
        LegB.addParent(this);
        LegC.addParent(this);

        Arrays.stream(points).collect(Collectors.toSet()).forEach(
                p -> p.attach(this)
        );

        setPivot(A.getStart().getPivot().add(B.getStart().getPivot()).add(C.getStart().getPivot()).div(3));
        calcNormal(A.getStart().getPivot(), B.getStart().getPivot(), C.getStart().getPivot());
        TriStateArea.register(this);
        drawDist();
        toConstraintObject();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Calculates the normal vector of the triangle given three vertices.
     * @param A The first vertex of the triangle.
     * @param B The second vertex of the triangle.
     * @param C The third vertex of the triangle.
     */
    public void calcNormal(Vector3 A, Vector3 B, Vector3 C) {
        Vector3 AB = B.sub(A);
        Vector3 AC = C.sub(A);
        normal = AB.cross(AC).normalize();
    }

    /**
     * Gets Leg A
     * @return GLine
     */
    public GLine getLegA() {
        return LegA;
    }

    /**
     * Gets Leg B
     * @return GLine
     */
    public GLine getLegB() {
        return LegB;
    }

    /**
     * Gets Leg C
     * @return GLine
     */
    public GLine getLegC() {
        return LegC;
    }

    /**
     * Calculates the area of the triangle.
     * @return The area.
     */
    public double area() {
        Vector3 A = LegA.getStart().getPivot();
        Vector3 B = LegB.getStart().getPivot();
        Vector3 C = LegC.getStart().getPivot();

        return Math.abs((B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX())) / 2;
    }

    @Override
    public String toString() {
        return col.toString() + " GTri";
    }

    /**
     * @implNote This also deletes it's child lines (if they arent parented to anything else) and unregisters itself
     * from the {@link TriStateArea}
     */
    @Override
    public boolean deleteSelf() {
        super.deleteSelf();
        getLegStream().forEach(
                line -> {
                    line.removeParent(this);
                    if (!line.hasParent()) line.deleteSelf();
                }
        );
        TriStateArea.unregister(this);
        Static.renderer.removeOverlap(getId());
        return true;
    }

    /**
     * Creates a stream of this triangle's edges.
     * @return A stream of GLines
     */
    public Stream<GLine> getLegStream() {
        return Stream.of(LegA, LegB, LegC);
    }

    @Override
    public ConstraintManager<GTri> getConstraints() {
        return constraints;
    }

    @Override
    public CTri toConstraintObject() {
        if (constraintObject == null) {
            constraintObject = new CTri(this);
        }
        return (CTri) constraintObject;
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
        Vector3 piv = getLegA().getStart().getPivot().add(getLegB().getStart().getPivot()).add(getLegC().getStart().getPivot()).div(3);
        if (!piv.equals(getDupeObjectToCheck())) {
            setPivot(piv);
            calcNormal(getLegA().getStart().getPivot(), getLegB().getStart().getPivot(), getLegC().getStart().getPivot());
        }
    }
}
