package com.j3d.engine.geometry.geo2d;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.layer.Layer;
import com.j3d.storage.files.ProjectFile;
import com.j3d.ui.util.Throbber;

import java.awt.*;
import java.util.ArrayList;
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
 *     A GPoint is also stored within {@link Renderer#points} for redundancy.
 * </p>
 * @see Renderer#findOrCreatePoint(Vector3, Layer)
 * @see Thing
 * @see GLine
 * @see GTri
 */
public class GPoint extends GObject {

    /**
     * The diameter of the point when drawn on screen.
     * <p>
     *     This should be used as the standard diameter for all points.
     * </p>
     */
    public static final int DIAMETER = 7;

    /**
     * Constructs a GPoint.
     * @implSpec This is used by {@link ProjectFile#readFile(String, String, Throbber)} during a project file read and should only be used in that case.
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
        Static.renderer.points.add(this);
        if (J3DSettings.getViewType() != ViewType.WIREFRAME) return;
        graphics2D.setColor(col);
        ScreenPoint p = this.getPivot().toPoint(Static.camera).toScreen(Static.renderer);
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
        if (J3DSettings.getViewType() != ViewType.WIREFRAME) return;
        graphics2D.setColor(Color.WHITE);
        ScreenPoint p = this.getPivot().toPoint(Static.camera).toScreen(Static.renderer);
        graphics2D.fillOval(p.x - (DIAMETER+1) / 2, p.y - (DIAMETER+1) / 2, (DIAMETER+1), (DIAMETER+1));
        draw(graphics2D);
    }

    /**
     * Default Constructor
     * @param v3 The position of this point.
     */
    public GPoint(Vector3 v3) {
        setPivot(v3);
    }

    @Override
    public boolean deleteSelf() {
        super.deleteSelf();
        Static.renderer.points.remove(this);
        return true;
    }

    @Override
    public ArrayList<Object> toArray() {
        ArrayList<Object> arr =  super.toArray();
        arr.addFirst("GPOINT");
        return arr;
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
}
