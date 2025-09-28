package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventType;
import com.j3d.engine.events.ObjectType;
import com.j3d.engine.geometry.base.CartesianPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * GLine represents, you guessed it, a line.
 */
public class GLine extends GObject {
    /**
     * The startpoint of this line
     */
    private CartesianPoint startPoint;
    /**
     * The endPoint of this line.
     */
    private CartesianPoint endPoint;
    
    @Override
    public void draw(Renderer renderer, Graphics2D graphics2D) {
        graphics2D.setColor(col);
        graphics2D.drawLine(
                startPoint.toScreen(renderer).x,
                startPoint.toScreen(renderer).y,
                endPoint.toScreen(renderer).x,
                endPoint.toScreen(renderer).y
        );
    }

//    private void drawLine(Renderer renderer, Graphics2D graphics2D,GPoint A, GPoint B) {
//        startPoint = A.getPivot();
//        endPoint = B.getPivot();
//        draw(renderer, graphics2D);
//    }

    /**
     * Updates the given GLine by redrawing itself and broadcasting the updates to parent Objects
     * @param renderer The Renderer Instance
     * @param A GPoint A
     * @param B GPoint A
     */
    private void update(Renderer renderer, GPoint A, GPoint B, GObject ...exclusions) {
        Event e = new Event(this, renderer, this.startPoint, this.endPoint, A.getPivot(), B.getPivot());
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.NODE_UPDATED, ObjectType.PARENT, e);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE, e);
        startPoint = A.getPivot();
        endPoint = B.getPivot();
//        drawLine(renderer, graphics2D, A, B);
    }

    /**
     * Default Constructor
     * @param renderer The Renderer
     * @param A The start point
     * @param B THe end point
     */
    public GLine(Renderer renderer, GPoint A, GPoint B) {
        super(renderer);
        startPoint = A.getPivot();
        endPoint = B.getPivot();
//        drawLine(renderer, graphics2D, A, B);

        attach(A, ObjectType.NODE);
        attach(B, ObjectType.NODE);
        A.attach(this, ObjectType.PARENT);
        B.attach(this, ObjectType.PARENT);

        // set the pivot to the midpoint of the line.
        setPivot(new CartesianPoint(
                (A.getPivot().x + B.getPivot().x) / 2,
                (A.getPivot().y + B.getPivot().y) / 2
        ));
    }


    /**
     * Event or sum ion know ganger ✌️
     */
    public static class Event extends EventBroadcast {

        public CartesianPoint newStart;
        public CartesianPoint oldStart;
        public CartesianPoint newEnd;
        public CartesianPoint oldEnd;

        /**
         * Constructor for EventBroadcast with a new start point
         *
         * @param e The initiator of the broadcast.
         * @param r The renderer instance
         * @param oS old starting point
         * @param oE old ending point
         * @param pts The new points, pass empty if you're not updating an end point.
         */
        public Event(EventEmitter e, Renderer r,CartesianPoint oS, CartesianPoint oE, CartesianPoint ...pts) {
            super(e, r);
            oldStart = oS;
            oldEnd = oE;
            boolean flip = true;
//            assert pts.length == 2;
            for (CartesianPoint pt : pts) {
                if (flip) {
                    newStart = pt;
                    flip = false;
                } else {
                    newEnd = pt;
                }
            }
        }
    }


//    public void setPivot(CartesianPoint pivot, Renderer r) {;
//        super.setPivot(pivot);
//    }

    @Override
    public boolean deleteSelf(Renderer renderer, GObject ...excluded) {
        Event e = new Event(this, renderer, this.startPoint, this.endPoint);
        e.exclusions.addAll(new ArrayList<>(Arrays.asList(excluded)));
        e.exclusions.add(this);
        broadcast(EventType.NODE_DELETED, ObjectType.PARENT, e);
        broadcast(EventType.PARENT_DELETED, ObjectType.NODE, e);
        return super.deleteSelf(renderer);
    }

    @Override
    public void onEvent(EventType event, EventBroadcast properties) {
        if (properties.exclusions.contains(this)) return;
        switch (event) {
            case NODE_UPDATED -> {
                // a node was updated
                if (Objects.requireNonNull(properties) instanceof GPoint.Event pe) {
                    System.out.println(pe.exclusions);
                    if (pe.oldCartesianPoint.equals(startPoint)) setStartPoint(pe.newCartesianPoint, pe.renderer, properties.exclusions.toArray(GObject[]::new));
                    else if (pe.oldCartesianPoint.equals(endPoint)) setEndPoint(pe.newCartesianPoint, pe.renderer, properties.exclusions.toArray(GObject[]::new));
                    else setPivot(pe.newCartesianPoint);
                } else {
                    throw new IllegalStateException("Unexpected value: " + properties);
                }
            }
            case NODE_DELETED -> {
                if (Objects.requireNonNull(properties) instanceof GPoint.Event pe) {
                    // A line requires 2 points to exist. Delete self if we cant exist.
                    deleteSelf(pe.renderer, properties.exclusions.toArray(GObject[]::new));
                }
            }
            case PARENT_DELETED -> {
                if (properties instanceof  GTri.Event) {
                    // the triangle got deleted, delete ourselves
                    deleteSelf(properties.renderer, properties.exclusions.toArray(GObject[]::new));
                }
            }
//            case PARENT_UPDATED -> {
//                // the triangle directly calls setEndPoint and setStartPoint. This isn't needed.
//            }
        }
    }

    /**
     * Sets the end point
     * @param end The end point
     * @param renderer The Renderer Instance.
     */
    public void setEndPoint(CartesianPoint end, Renderer renderer, GObject... exclusions) {
        Event e =  new Event(this, renderer, this.startPoint, this.endPoint, new CartesianPoint(), end);
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE,e);
        this.endPoint = end;
    }

    /**
     * Returns the end point
     * @return CartesianPoint
     */
    public CartesianPoint getEndPoint() {
        return endPoint;
    }

    /**
     * Sets the start point
     * @param start The start point
     * @param renderer The Renderer Instance.
     */
    public void setStartPoint(CartesianPoint start, Renderer renderer, GObject... exclusions) {
        Event e = new Event(this, renderer, this.startPoint, this.endPoint, start, new CartesianPoint());
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE, e);
        this.startPoint = start;
    }

    /**
     * Returns the start point
     * @return start point.
     */
    public CartesianPoint getStartPoint() {
        return startPoint;
    }

    /**
     * Calculates and returns the length of this line.
     * @return a double representing the length of the line.
     */
    public double length() {
        return Math.sqrt(
                Math.pow(startPoint.x- endPoint.x, 2) + Math.pow(startPoint.y- endPoint.y, 2)
        );
    }
}
