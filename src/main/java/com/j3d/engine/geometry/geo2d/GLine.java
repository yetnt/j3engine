package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.Renderer;
import com.j3d.engine.react.events.EventBroadcast;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;

import static com.j3d.ui.engine.EngineFrame.camera;
import static com.j3d.ui.engine.EngineFrame.renderer;

/**
 * GLine represents, you guessed it, a line.
 */
public class GLine extends GObject {
    /**
     * The startpoint of this line
     */
    private final GPoint startPoint;
    /**
     * The endPoint of this line.
     */
    private final GPoint endPoint;
    
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

    @Override
    public boolean deleteSelf() {
        this.startPoint.deleteSelf();
        this.endPoint.deleteSelf();
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
}
