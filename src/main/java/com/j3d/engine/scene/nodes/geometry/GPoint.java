package com.j3d.engine.scene.nodes.geometry;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.HasParents;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.geometry.Point;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.dialog.Spinner;

import java.util.*;

/**
 * GPoint is a class that represents a single point in 3D space.
 * <p>
 *     Unlike other GObjects who's {@code pivot} has to be calculated to be
 *     it's geometric centre, a GPoint's pivot is just its position.
 * </p>
 * <p>
 *     While a GPoint can only be drawn by anther {@link GLine}, a {@link Thing}
 *     stores GPoints separately as they form the core
 *     of any transformation irrespective of whether it's part of some
 *     other geometry. This means all transformations need to be point based
 *     and applied to the GPoint.
 * </p>
 * <p>
 *     A GPoint is also stored within {@link SceneManager#points} for redundancy.
 * </p>
 * @see Thing
 * @see GLine
 * @see GTri
 */
public class GPoint extends GObject implements HasParents<GObject>, DecomposeWhenDrawn<Point> {

    /**
     * The diameter of the point when drawn on screen.
     * <p>
     *     This should be used as the standard diameter for all points.
     * </p>
     */
    public static final int DIAMETER = 7;
    private HashSet<GObject> parents = new HashSet<>();

    /**
     * Constructs a GPoint.
     * @implSpec This is used by {@link ProjectFile#readFile(String, String, Spinner)} during a project file read and should only be used in that case.
     * @param id The ID of the GPoint defined by the file
     * @param point The position of the GPoint defined by the file
     * @return A GPoint
     */
    public static GPoint fromRaw(String id, Vector3 point) {
        GPoint gp = new GPoint(point);
        gp.setId(UUID.fromString(id));
        return gp;
    }

    @Override
    public void setPivot(Vector3 pivot) {
        super.setPivot(pivot);
        invalidateAll();
        decompose();
        this.broadcast(EventType.GPOINT_RECALC_PIVOT, new GPointMovedEvent(this, StaticRefs.getSceneManager()));
    }

    /**
     * Default Constructor
     * @param v3 The position of this point.
     */
    public GPoint(Vector3 v3) {
        StaticRefs.getSceneManager().points.add(this);
        setPivot(v3);
        StaticRefs.getSceneManager().hasNoParent(this);
        addProps();
    }

    public Point toPoint() {
        return Point.from(getPivot());
    }

    private void addProps() {
        // technically doesnt add additional props from GObject since its pivot is provided already
        // which is just the point itself.
    }

    @Override
    public boolean deleteSelf() {
        super.deleteSelf();
        StaticRefs.getSceneManager().points.remove(this);
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GPoint other)) return false;
        return Objects.equals(this.getPivot(), other.getPivot());
    }

    @Override
    public String toString() {
        return "GPoint {" + getPivot().getX() + ", " + getPivot().getY() +  ", " + getPivot().getZ() + "}";
    }

    @Override
    public HashSet<GObject> getParents() {
        return parents;
    }

    @Override
    public void addParent(GObject parent) {
        parents.add(parent);
        StaticRefs.getSceneManager().hasParent(this);
    }

    @Override
    public void removeParent(GObject parent) {
        boolean su = parents.remove(parent);
        StaticRefs.getSceneManager().hasNoParent(this);
    }

    @Override
    public void copy(CopyProperties props) throws InvalidCopyException {
        // no special logic.s
        props.add(getId(), copySelf());
    }

    protected GPoint copySelf() {
        GPoint p = new GPoint(
                getPivot()
        );
        p.setColour(getColour());
        return p;
    }

    public void explode(GLine line) {
        line.detachListener(this);
        this.detachListener(line);
        removeParent(line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    private RenderState<Point, GObject> renderState;
    @Override
    public void decompose() {
        if (renderState != null) renderState.invalidate();
        if (renderState == null || !renderState.isValid()) {
            renderState = toPoint().toRenderState(this);
        }
    }

    @Override
    public ArrayList<RenderState<Point, GObject>> getDecomposeList() {
        if (renderState == null) return new ArrayList<>();
        return new ArrayList<>(Collections.singletonList(renderState));
    }

    @Override
    public ArrayList genericRenderStateList() {
        decompose();
        return getDecomposeList();
    }

    public static class GPointMovedEvent extends EventPayload<GPoint> {
        public GPointMovedEvent(GPoint e, SceneManager r) {
            super(e);
        }
    }

}
