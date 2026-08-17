package com.j3d.engine.scene.nodes.geometry;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.payloads.GPointMovedEvent;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.draw.SortMethod;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.scene.nodes.Thing;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.properties.Property;
import com.j3d.StaticConfig;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.storage.files.protocol.proj.PF1;
import com.j3d.ui.dialog.Spinner;

import static com.j3d.StaticRefs.getSceneManager;

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
 *     it is also stored within {@link SceneRenderer} for draw ordering.
 * </p>
 * @implSpec Triangles rely on {@link Winding} order to define normal calculations and
 * back face culling via {@link SortMethod#backFaceCulled(GTri)}.
 * Normals are expected to point outward from solids.
 * @author Lehlogonolo Poole
 * @see SceneRenderer
 * @see Thing
 * @see GPoint
 * @see GLine
 */
public class GTri extends GObject implements IdempotentEventListener<GPointMovedEvent, Vector3>, DecomposeWhenDrawn<Triangle> {
    /**
     * Leg A, connected to Leg B and Leg C
     */
    protected GLine LegA;
    /**
     * Leg B, connected to Leg A and Leg C
     */
    protected GLine LegB;
    /**
     * Leg C, connected to Leg A and Leg B
     */
    protected GLine LegC;

    /**
     * The winding of the triangle
     */
    protected Winding winding;

    /**
     * Whether this triangle is double sided or not. If it is, it's unaffected by back-face culling.
     * (Infinite 2d surface)
     */
    private boolean doubleSided = true;

    protected boolean deletedState = false;

    // TODO: I actually have no clue where the fuck this is used?? Uhm finder this out??
    private boolean hidden = false;

    /**
     * Constructs a GTri.
     * @implSpec This is used by {@link PF1#readFile(String, String, Spinner)} during a project file read and should only be used in that case.
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

    public static GTri fromV3Raw(String id, Color col, GLine gLine, GLine gLine1, GLine gLine2, GPoint gPoint, GPoint gPoint1, GPoint gPoint2) {
        GTri gt = new GTri(col, gLine, gLine1, gLine2, new Winding(gPoint, gPoint1, gPoint2));
        gt.setId(UUID.fromString(id));
        return gt;
    }

    /**
     * Constructs a new GTri from 3 points.
     *
     * @param c The colour
     * @param A Point A
     * @param B Point B
     * @param C Point C
     *
     * @implSpec This constructor is a convenience constructor to {@link #GTri(Color, GLine, GLine, GLine)}
     * which creates the lines from 3 points. However it's easy to forget that the constructor
     * implicitly creates attitudinal GLines that still need to be parented to a
     * {@link Thing}. Forgetting this would break {@link ProjectFile} serialization.
     * if possible rather use {@link #GTri(Color, GLine, GLine, GLine)}, or otherwise
     * make use of the ine accessor methods to get the created GLines
     * @implNote Winding is implied obviously by the order of the points given.
     * @see ProjectFile
     * @see Thing
     * @see GLine
     */
    public GTri(Color c, GPoint A, GPoint B, GPoint C) {
        super(c);

        LegA = new GLine(A, B);
        LegB = new GLine(B, C);
        LegC = new GLine(C, A);
        setWinding(A, B, C);

        LegA.addParent(this);
        LegB.addParent(this);
        LegC.addParent(this);

        A.addParents(LegC, LegA);
        B.addParents(LegA, LegB);
        C.addParents(LegB, LegC);

        A.attachListener(this);
        B.attachListener(this);
        C.attachListener(this);

        setPivot(A.getPivot().add(B.getPivot()).add(C.getPivot()).div(3));

        normal();

//        SceneRenderer.register(toRenderState());
        drawDist();
        addProps();
    }

    public Triangle toTriangle() {
        return new Triangle(
                winding.first().getPivot(),
                winding.second().getPivot(),
                winding.third().getPivot()
        );
    }

    private RenderState<Triangle, GObject> renderState;
    public RenderState<Triangle, GObject> toRenderState() {
        if (renderState != null) renderState.invalidate();
        if (renderState == null || !renderState.isValid()) {
            renderState = toTriangle().toRenderState(this);
        }
        return renderState;
    }

    /**
     * Constructs a new GTri from 3 lines.
     * @implSpec The lines have to connect in a counterclockwise order or clockwise order.
     * In that, 2 lines cannot have the same start point or end points.
     * @param c The colour.
     * @param A The first line.
     * @param B The second line.
     * @param C The third line.
     * @implSpec This constructor assumes winding by checking that each edge's start point is unique
     * and similar to the end point.
     */
    public GTri(Color c, GLine A, GLine B, GLine C) {
        super(c);
        GPoint[] points = {
                A.getA(), A.getB(),
                B.getA(), B.getB(),
                C.getA(), C.getB()
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
                p -> p.attachListener(this)
        );

        setWinding(points[0], points[2], points[4]);


        setPivot(Vector3.reduceToVector3(
                winding.toVector3List()
                , Vector3::add).div(3));
        normal();
        StaticRefs.getSceneManager().getRenderer().register(toRenderState());
        drawDist();
        addProps();
    }

    /**
     * Constructs a new GTri from a winding and 3 lines.
     * @param c The colour.
     * @param A The first line.
     * @param B The second line.
     * @param C The third line.
     * @param winding The winding of the triangle.
     * @implSpec This constructor is primarily used for deserialization purposes where the winding is already known.
     */
    public GTri(Color c, GLine A, GLine B, GLine C, Winding winding) {
        super(c);

        LegA = A;
        LegB = B;
        LegC = C;

        this.winding = winding;

        LegA.addParent(this);
        LegB.addParent(this);
        LegC.addParent(this);

        setPivot(Vector3.reduceToVector3(winding.toVector3List(), Vector3::add).div(3));
        normal();
        StaticRefs.getSceneManager().getRenderer().register(toRenderState());
        drawDist();
        addProps();
    }

    private void addProps() {
        properties.addAll(List.of(
                new Property<>("Tri Normal", this::normal, GTri.class)
                        .holds(Vector3.class)
                        .setDescription("The normal of this triangle").constant(),
                new Property<>("Leg A", this::getLegA, GTri.class)
                        .holds(GLine.class)
                        .setDescription("The first leg of this triangle").constant(),
                new Property<>("Leg B", this::getLegB, GTri.class)
                        .holds(GLine.class)
                        .setDescription("The second leg of this triangle").constant(),
                new Property<>("Leg C", this::getLegC, GTri.class)
                        .holds(GLine.class)
                        .setDescription("The third leg of this triangle").constant(),
                new Property<>("Double Sided", this::isDoubleSided, GTri.class)
                        .setNewValueConsumer(this::setDoubleSided)
                        .holds(Boolean.class)
                        .setDescription("Whether this triangle is double sided or not")
//                        .constant()
        ));
        pivotProperty.constant(); // cannot be edited.
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Calculates the normal vector of the triangle given three vertices.
     */
    public Vector3 normal() {
        return winding.normal();
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
        return Triangle.area(
                winding.first().getPivot(),
                winding.second().getPivot(),
                winding.third().getPivot()
        );
    }

    /**
     * Calculates the depth of the tri relative to the camera's forward direction.
     * @return The depth value.
     */
    public double calcDepth() {
        Vector3 toTri = getPivot().sub(StaticRefs.getCamera().getPosition());
        return toTri.dot(StaticRefs.getCamera().getForward().normalize());
    }

    /**
     * Calculates the Euclidean distance from the triangle's pivot to the camera position.
     * @return The Euclidean distance.
     */
    public double euclideanDist() {
        return getPivot().sub(StaticRefs.getCamera().getPosition()).magnitude();
    }

    public void setWinding(GPoint A, GPoint B, GPoint C) {
        winding = new Winding(A, B, C);
    }
    public Winding getWinding() {
        return winding;
    }

    @Override
    public void copy(CopyProperties props) throws InvalidCopyException {
        GLine A = GLine.getOrCreateCopy(props, LegA);
        GLine B = GLine.getOrCreateCopy(props, LegB);
        GLine C = GLine.getOrCreateCopy(props, LegC);
        props.add(getId(), copy(A, B, C, getWindingFromCopy(props)));
    }

    private GTri copy(GLine lnA, GLine lnB, GLine lnC, Winding winding) {
        GTri tri = new GTri(getColour(), lnA, lnB, lnC, winding);
        tri.setHidden(isHidden());
        return tri;
    }

    private Winding getWindingFromCopy(CopyProperties properties) {
        Function<UUID, GPoint> getter = id -> {
            GObject o = properties.getCopies().stream()
                    .filter(copy -> copy.original().equals(id))
                    .collect(Collectors.toCollection(ArrayList::new))
                    .getFirst()
                    .copy();
            return (GPoint) o;
        };
        GPoint a = getter.apply(winding.first().getId());
        GPoint b = getter.apply(winding.second().getId());
        GPoint c = getter.apply(winding.third().getId());

        return new Winding(a, b, c);
    }

    @Override
    public String toString() {
        return col.toString() + " GTri";
    }

    /**
     * @implNote This also deletes it's child lines (if they arent parented to anything else) and unregisters itself
     * from the {@link SceneRenderer}
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
        getSceneManager().removeOverlap(getId());
        return true;
    }

    /**
     * Checks if this triangle is double-sided. Double-sided triangles are unaffected by back-face culling.
     * @return {@code true} if the triangle is double-sided, {@code false} otherwise.
     */
    public boolean isDoubleSided() {
        return doubleSided;
    }

    /**
     * Sets whether this triangle is double-sided.
     * @param doubleSided {@code true} to make the triangle double-sided, {@code false} for single-sided.
     */
    public void setDoubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
    }

    /**
     * Creates a stream of this triangle's edges.
     * @return A stream of GLines
     */
    public Stream<GLine> getLegStream() {
        return Stream.of(LegA, LegB, LegC);
    }

    @Override
    public void onEvent(EventType event, EventPayload properties) {
        if (event == EventType.GPOINT_RECALC_PIVOT && properties instanceof GPointMovedEvent p)
            handlePossibleDuplicates(event, p);
    }

    @Override
    public Vector3 getDupeObjectToCheck() {
        return getPivot();
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPointMovedEvent payload) {
        Vector3 piv = getLegA().getA().getPivot().add(getLegB().getA().getPivot()).add(getLegC().getA().getPivot()).div(3);
        if (!piv.equals(getDupeObjectToCheck())) {
            setPivot(piv);
            normal();
            invalidateAll();
            decompose();
        }
    }

    public ArrayList<GPoint> explode() {
        deletedState = true;
        ArrayList<GPoint> points = new ArrayList<>();
        getLegStream().forEach(line -> {
           ArrayList<GPoint> pointStream = line.explode(this);
           line.removeParent(this);
           points.addAll(pointStream);
           detachListener(line);
           line.getPointStream().forEach(this::detachListener);
        });
        points.forEach(p -> {
            if (p != null) {
                this.detachListener(p);
                p.detachListener(this);
            }
        });
        LegA = null;
        LegB = null;
        LegC = null;

        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GTri gTri = (GTri) o;
        return deletedState == gTri.deletedState &&
                isHidden() == gTri.isHidden();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deletedState, isHidden(), doubleSided);
    }
    public boolean showNorm = false;

    @Override
    public boolean isDeletedState() {
        return deletedState;
    }

    @Override
    public void decompose() {
        toRenderState();
    }

    @Override
    public ArrayList<RenderState<Triangle, GObject>> getDecomposeList() {
        if (renderState == null) return new ArrayList<>();
        return new ArrayList<>(Collections.singletonList(renderState));
    }

    @Override
    public ArrayList genericRenderStateList() {
        decompose();
        return getDecomposeList();
    }
    /**
     * Private method to draw the triangle's distance depth and normal overlays.
     */
    private void drawDist() {
        getSceneManager().scheduleOverlap(getId(), g -> {
                    if (StaticConfig.isShowTriDistances()) {
                        // draw text showing the tris distance from camera
                        Vector3 triCentroid = this.getPivot();
                        getSceneManager().drawText3D(g, triCentroid,
                                String.format("Dist: %.2f", this.getPivot().sub(StaticRefs.getCamera().getPosition()).magnitude()),
                                StaticRefs.getCamera(),
                                new Color(0, 0, 0),
                                this.getColour());
                    }
                    if (StaticConfig.isShowDepth()) {
                        // draw text showing the tris depth from camera
                        Vector3 triCentroid = this.getPivot();
                        double depth = this.calcDepth();
                        getSceneManager().drawText3D(g, triCentroid.add(new Vector3(1, 0, 0)),
                                String.format("Depth: %.2f", depth),
                                StaticRefs.getCamera(),
                                new Color(0, 0, 0),
                                this.getColour());
                    }
                    if (StaticConfig.isShowNormals() || showNorm) {
                        // draw text showing the tris normal
                        Vector3 triCentroid = this.getPivot();
//                        StaticRefs.getSceneManager().drawText3D(g, triCentroid.sub(new Vector3(4, 0, 0)),
//                                String.format("Normal: (%.2f, %.2f, %.2f)", normal().getX(), normal().getY(), normal().getZ()),
//                                StaticRefs.getCamera(),
//                                new Color(0, 0, 0),
//                                this.getColour());
                        // The following code draws the normal
                        g.setColor(Color.RED);
                        getSceneManager().drawLine3D(g, getPivot(), getPivot().add(normal().mult(0.5)), StaticRefs.getCamera());
                    }
                }
        );
    }

}
