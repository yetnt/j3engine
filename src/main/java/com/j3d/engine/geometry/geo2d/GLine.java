package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventType;
import com.j3d.engine.events.ObjectType;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * GLine represents, you guessed it, a line.
 */
public class GLine extends GObject {
    /**
     * The startpoint of this line
     */
    private GPoint startPoint;
    /**
     * The endPoint of this line.
     */
    private GPoint endPoint;
    
    @Override
    public void draw(Renderer renderer, Graphics2D graphics2D, Camera cam) {
        graphics2D.setColor(col);
        graphics2D.drawLine(
                startPoint.getPivot().toPoint2(cam).toScreen(renderer).x,
                startPoint.getPivot().toPoint2(cam).toScreen(renderer).y,
                endPoint.getPivot().toPoint2(cam).toScreen(renderer).x,
                endPoint.getPivot().toPoint2(cam).toScreen(renderer).y
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
        Event e = new Event(this, renderer, this.startPoint.getPivot(), this.endPoint.getPivot(), A.getPivot(), B.getPivot());
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.NODE_UPDATED, ObjectType.PARENT, e);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE, e);
        startPoint = A;
        endPoint = B;
//        drawLine(renderer, graphics2D, A, B);
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
//        drawLine(renderer, graphics2D, A, B);

        attach(A, ObjectType.NODE);
        attach(B, ObjectType.NODE);
        A.attach(this, ObjectType.PARENT);
        B.attach(this, ObjectType.PARENT);

        // set the pivot to the midpoint of the line
        setPivot(new Vector3(
                (A.getPivot().getX() + B.getPivot().getX()) / 2,
                (A.getPivot().getY() + B.getPivot().getY()) / 2,
                0
        ));

    }


    /**
     * Event or sum ion know ganger ✌️
     */
    public static class Event extends EventBroadcast {

        public Vector3 newStart;
        public Vector3 oldStart;
        public Vector3 newEnd;
        public Vector3 oldEnd;

        /**
         * Constructor for EventBroadcast with a new start point
         *
         * @param e The initiator of the broadcast.
         * @param r The renderer instance
         * @param oS old starting point
         * @param oE old ending point
         * @param pts The new points, pass empty if you're not updating an end point.
         */
        public Event(EventEmitter e, Renderer r, Vector3 oS, Vector3 oE, Vector3 ...pts) {
            super(e, r);
            oldStart = oS;
            oldEnd = oE;
            boolean flip = true;
//            assert pts.length == 2;
            for (Vector3 pt : pts) {
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
        Event e = new Event(this, renderer, this.startPoint.getPivot(), this.endPoint.getPivot());
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
                    if (pe.oldCartesianPoint.equals(startPoint.getPivot())) setStartPoint(pe.newCartesianPoint, pe.renderer, properties.exclusions.toArray(GObject[]::new));
                    else if (pe.oldCartesianPoint.equals(endPoint.getPivot())) setEndPoint(pe.newCartesianPoint, pe.renderer, properties.exclusions.toArray(GObject[]::new));
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
    public void setEndPoint(Vector3 end, Renderer renderer, GObject... exclusions) {
        Event e =  new Event(this, renderer, this.startPoint.getPivot(), this.endPoint.getPivot(), new Vector3(), end);
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE,e);
        this.endPoint.setPivot(end);
    }

    /**
     * Returns the end point
     * @return CartesianPoint
     */
    public Vector3 getEndPoint() {
        return endPoint.getPivot();
    }


    /**
     * Returns the end GPoint
     * @return GPoint
     */
    public GPoint getEnd() {
        return endPoint;
    }

    /**
     * Sets the start point
     * @param start The start point
     * @param renderer The Renderer Instance.
     */
    public void setStartPoint(Vector3 start, Renderer renderer, GObject... exclusions) {
        Event e = new Event(this, renderer, this.startPoint.getPivot(), this.endPoint.getPivot(), start, new Vector3());
        e.exclusions.addAll(Arrays.asList(exclusions));
        e.exclusions.add(this);
        broadcast(EventType.PARENT_UPDATED, ObjectType.NODE, e);
        this.startPoint.setPivot(start);
    }

    /**
     * Returns the start point
     * @return start point.
     */
    public Vector3 getStartPoint() {
        return startPoint.getPivot();
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
}
