package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventType;
import com.j3d.engine.events.ObjectType;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.ScreenPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

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

    /**
     * Default Constructor
     * @param renderer The Renderer Instance
     * @param cartesianPoint The point on the cartesian plane that this object lies on
     */
    public GPoint(Renderer renderer, CartesianPoint cartesianPoint) {
        super(renderer);
        setPivot(cartesianPoint);
    }

    /**
     * This represents an event that is broadcasted when a GPoint is updated and/or deleted.
     */
    public static class Event extends EventBroadcast {

        /**
         * The new location of the GPoint
         */
        public final CartesianPoint newCartesianPoint;
        /**
         * The old location of the GPoint
         */
        public final CartesianPoint oldCartesianPoint;

        /**
         * Default Constructor
         * @param e The emitterPoint
         * @param old The Old Cartesian Point
         * @param cp The New Cartesian Point
         */
        public Event(EventEmitter e,CartesianPoint old, CartesianPoint cp, Renderer r) {
            super(e, r);
            oldCartesianPoint = old;
            newCartesianPoint = cp;
        }
    }

    /**
     * Updates the given GPoint by redrawing itself and broadcasting the updates to parent Objects
     * @param renderer The Renderer Instance
     * @param pivot The point on the cartesian plane.
     */
    private void update(Renderer renderer, CartesianPoint pivot) {
        broadcast(EventType.NODE_UPDATED, ObjectType.PARENT, new Event(this, getPivot(), pivot, renderer));
//        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE, new Event(this, getPivot(), pivot, renderer));
        // The above line of code has been commented out as a point cannot be a parent
        super.setPivot(pivot);
    }

    @Override
    public boolean deleteSelf(Renderer renderer) {
        broadcast(EventType.NODE_DELETED, ObjectType.PARENT, new Event(this, getPivot(), new CartesianPoint(), renderer));
        return super.deleteSelf(renderer);
    }

    @Override
    public void onEvent(EventType event, EventBroadcast properties) {
        switch (event) {
            case PARENT_DELETED -> {
                if (Objects.requireNonNull(properties) instanceof GLine.Event gl) {
                    GLine line = (GLine) gl.emitter;
                    // Detach self from parent
                    line.detach(this, ObjectType.NODE);
                    if (registeredParents() == 1) {
                        // Only delete self, if there is only 1 parent.
                        // meaning it was that deleted line
                        deleteSelf(gl.renderer);
                        // otherwise, it may have multiple parents meaning its connected
                        // to some other line so dont delete it.
                    }
                    // Detach parent from self
                    detach(line, ObjectType.PARENT);
                } else {
                    throw new IllegalStateException("Unexpected value: " + properties);
                }
            }
            case PARENT_UPDATED -> { // A parent was updated, we need to update ourselves
                if (Objects.requireNonNull(properties) instanceof GLine.Event gl) {// A line was updated, update ourselves.
                    // Find which point
                    CartesianPoint pt = new CartesianPoint();
                    // Check if the start is non-null and whether the old start does equal the current
                    if (gl.newStart.isNotEmpty() && gl.oldStart.equals(getPivot())) pt = gl.newStart;
                    // same as above.
                    if (gl.newEnd.isNotEmpty() && gl.oldEnd.equals(getPivot())) pt = gl.newEnd;
                    if (pt.isNotEmpty()) this.update(gl.renderer, pt);
                } else {
                    throw new IllegalStateException("Unexpected value: " + properties);
                }
            }
            case PARENT_EXPLODED -> {
                if (Objects.requireNonNull(properties) instanceof GLine.Event gl) {// A line was exploded, we need to detach ourselves from it.
                    GLine line = (GLine) gl.emitter;
                    // Detach self from parent (The parent is to have already done this after emitting the event)
//                    line.detach(this, ObjectType.NODE);
                    // Detach parent from self
                    detach(line, ObjectType.PARENT);
                } else {
                    throw new IllegalStateException("Unexpected value: " + properties);
                }
            }
        }
    }

    @Override
    public void draw(Renderer renderer, Graphics2D graphics2D) {
        ScreenPoint p = this.getPivot().toScreen(renderer);
        graphics2D.fillOval(p.x - DIAMETER / 2, p.y - DIAMETER / 2, DIAMETER, DIAMETER);
    }

//    /**
//     * A private method that actually draws the point.
//     * This method is private as it always draws the point irrespective of event listeners and other update signalers
//     * @param renderer The Renderer Instance
//     * @param cartesianPoint The actual point.
//     */
//    private void drawPoint(Renderer renderer,Graphics2D graphics2D, CartesianPoint cartesianPoint) {
//        setPivot(cartesianPoint);
//        draw(renderer, graphics2D);
//    }

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
}
