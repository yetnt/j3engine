package com.j3d.engine.geometry.geo2d;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * GPoint is a class that represents a single point in 2D space.
 */
public class GPoint extends GObject {

    /**
     * The diameter of the point when drawn on screen.
     * <p>
     *     This should be used as the standard diameter for all points.
     * </p>
     */
    public static final int DIAMETER = 7;

    public static GPoint fromRaw(String id, Vector3 point) {
        GPoint gp = new GPoint(point);
        gp.setId(UUID.fromString(id));
        return gp;
    }
    @Override
    public void draw(Graphics2D graphics2D) {
        Static.renderer.points.add(this);
        if (J3DSettings.getViewType() != ViewType.WIREFRAME) return;
        graphics2D.setColor(col);
        ScreenPoint p = this.getPivot().toPoint(Static.camera).toScreen(Static.renderer);
        graphics2D.fillOval(p.x - DIAMETER / 2, p.y - DIAMETER / 2, DIAMETER, DIAMETER);
    }

    @Override
    public void drawSelected(Graphics2D graphics2D) {
        if (J3DSettings.getViewType() != ViewType.WIREFRAME) return;
        graphics2D.setColor(Color.WHITE);
        ScreenPoint p = this.getPivot().toPoint(Static.camera).toScreen(Static.renderer);
        graphics2D.fillOval(p.x - (DIAMETER+1) / 2, p.y - (DIAMETER+1) / 2, (DIAMETER+1), (DIAMETER+1));
        draw(graphics2D);
//        EngineFrame.renderer.drawText3D(graphics2D, getPivot().sub(new Vector3(1, 1, 1)), "{" + getPivot().getY() + ", " + getPivot().getX() + ", " + getPivot().getZ() + "}", EngineFrame.camera);
    }

    /**
     * Default Constructor
     *
     * @param cartesianPoint The point on the cartesian plane that this object lies on
     */
    public GPoint(Vector3 cartesianPoint) {
        setPivot(cartesianPoint);
    }

    /**
     * This represents an event that is broadcasted when a GPoint is updated and/or deleted.
     */
    public static class Event extends EventPayload {

        /**
         * The new location of the GPoint
         */
        public final Vector3 newCartesianPoint;
        /**
         * The old location of the GPoint
         */
        public final Vector3 oldCartesianPoint;

        /**
         * Default Constructor
         * @param e The emitterPoint
         * @param old The Old Cartesian Point
         * @param cp The New Cartesian Point
         */
        public Event(EventEmitter e,Vector3 old, Vector3 cp, Renderer r) {
            super(e, r);
            oldCartesianPoint = old;
            newCartesianPoint = cp;
        }
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
