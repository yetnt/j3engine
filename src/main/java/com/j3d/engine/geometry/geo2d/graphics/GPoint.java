package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo2d.copy.CanCopy;
import com.j3d.engine.geometry.geo2d.copy.CopyProperties;
import com.j3d.engine.geometry.geo2d.copy.InvalidCopyException;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.StaticConfig;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.dialog.Spinner;

import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

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
 * @see SceneManager#findOrCreatePoint(Vector3, Layer)
 * @see Thing
 * @see GLine
 * @see GTri
 */
public class GPoint extends GObject implements HasParents<GLine> {

    /**
     * The diameter of the point when drawn on screen.
     * <p>
     *     This should be used as the standard diameter for all points.
     * </p>
     */
    public static final int DIAMETER = 7;
    private HashSet<GLine> parents = new HashSet<>();

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

    /**
     * Draws this point to the screen.
     * @implSpec This is only called by {@link GLine#draw(Graphics2D)}
     * @implNote As defined by {@link ViewType}, the point may or may not
     * be drawn. e.g. If not defined as {@link ViewType#WIREFRAME} then the point
     * skips drawing itself.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        if (StaticConfig.getViewType() != ViewType.WIREFRAME)
            if (hasParent() /*&& getParents().stream().findAny().get().hasParent()*/)
                return;

        if (StaticRefs.getSceneManager().getSelected().contains(this)) {
            drawSelected(graphics2D);
            return;
        }
        graphics2D.setColor(col);
        ScreenPoint p = this.getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager());
        graphics2D.fillOval(p.x - DIAMETER / 2, p.y - DIAMETER / 2, DIAMETER, DIAMETER);
    }

    /**
     * Draws this point to the screen in its selected state.
     * @implSpec This is only called by {@link GLine#drawSelected(Graphics2D)}
     * @implNote As defined by {@link ViewType}, the point may or may not
     * be drawn. e.g. If not defined as {@link ViewType#WIREFRAME} then the point
     * skips drawing itself.
     * @param graphics2D The Graphics2D instance
     */
    @Override
    public void drawSelected(Graphics2D graphics2D) {
        super.drawSelected(graphics2D);
        if (StaticConfig.getViewType() != ViewType.WIREFRAME)
            if (hasParent() /*&& getParents().stream().findAny().get().hasParent() */) {
                return;
            }
        graphics2D.setColor(Color.WHITE);
        ScreenPoint p = this.getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager());
        graphics2D.fillOval(p.x - (DIAMETER+1) / 2, p.y - (DIAMETER+1) / 2, (DIAMETER+1), (DIAMETER+1));
//        draw(graphics2D);
    }

    @Override
    public void setPivot(Vector3 pivot) {
        super.setPivot(pivot);
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
    public HashSet<GLine> getParents() {
        return parents;
    }

    @Override
    public void addParent(GLine parent) {
        parents.add(parent);
        StaticRefs.getSceneManager().hasParent(this);
    }

    @Override
    public void removeParent(GLine parent) {
        boolean su = parents.remove(parent);
        StaticRefs.getSceneManager().hasNoParent(this);
    }

    @Override
    public void copy(CopyProperties props) throws InvalidCopyException {
        //
    }

    public GPoint copy() {
        GPoint p = new GPoint(
                getPivot()
        );
        p.setColour(getColour());
        return p;
    }

    public void explode(GLine line) {
        line.detach(this);
        this.detach(line);
        removeParent(line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    public static class GPointMovedEvent extends EventPayload<GPoint> {
        public GPointMovedEvent(GPoint e, SceneManager r) {
            super(e, r);
        }
    }

}
