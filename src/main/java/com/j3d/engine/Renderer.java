package com.j3d.engine;

import com.j3d.engine.geometry.GLine;
import com.j3d.engine.geometry.GObject;
import com.j3d.engine.geometry.GPoint;
import com.j3d.engine.geometry.base.*;
import com.j3d.engine.geometry.base.Dimension;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;

/**
 * Renderer is a class responsible for the creation of {@link GObject}s and handling of what's going on in the
 * render. It's passed by instance allowing every {@link GObject} to use {@link Renderer#graphics}
 * @see Graphics2D
 */
public class Renderer {
    /**
     * The graphics given by the {@link javax.swing.JPanel#paintComponents(Graphics)}
     */
    private Graphics2D graphics;
    /**
     * The dimensions of the window.
     */
    public Dimension screenSize;
    /**
     * An ArrayDeque of objects in this render.
     */
    private ArrayDeque<GObject> gObjects = new ArrayDeque<>();
    /**
     * Factor to scale the {@link CartesianPoint} vs {@link ScreenPoint} units.
     * <p>
     * This is such that the screen space is not used as the default grid. Where (0, 1) and (0, 0) are but a pixel apart.
     * The Scale factor helps by making it such that (if SCALE is set to 10), inputting (0, 1) as a {@link CartesianPoint}, when converted to {@link ScreenPoint} it is multiplied by 10 units.
     */
    public double SCALE = 10.0;
    /**
     * Unused as of now.
     */
    public Point cameraOffset = new Point(0, 0);

    /**
     * Default Constructor
     * @param g The graphics
     * @param dim The dimensions of the screen
     */
    public Renderer(Graphics g, Dimension dim) {
        graphics = (Graphics2D) g;
        screenSize = dim;
    }

    /**
     * Create a new GLine from 2 CartesianPoints
     * @param A Point 1
     * @param B Point 2
     * @return A new GLine.
     */
    public GLine line(CartesianPoint A, CartesianPoint B) {
        GPoint gPointA = findOrCreatePoint(A);
        GPoint gPointB = findOrCreatePoint(B);
        GLine line = new GLine(this, gPointA, gPointB);
        gObjects.add(line);
        return line;
    }

    /**
     * Create a new point from one CartesianPoint
     * @param point Point 1
     * @return A new GPoint
     */
    public GPoint point(CartesianPoint point) {
        GPoint p = new GPoint(this, point);
        gObjects.add(p);
        return p;
    }

    /**
     * Draws the Cartesian XY Axis at play.
     */
    public void axis() {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw X axis (horizontal)
        graphics.drawLine(0, screenSize.height / 2, screenSize.width,  screenSize.height / 2);

        // Draw Y axis (vertical)
        graphics.drawLine(screenSize.width / 2, 0, screenSize.width / 2, screenSize.height);

        // Optional: draw origin marker
        graphics.setColor(Color.RED);
        graphics.fillOval(screenSize.width / 2 - 3, screenSize.height / 2 - 3, 6, 6);

        graphics.setColor(Color.BLACK);
    }

    /**
     * "Clears" the screen by drawing a white box over it.
     */
    public void clear() {
        // Optional: manually clear with a color
        graphics.setColor(Color.WHITE); // or whatever your background is
        graphics.fillRect(0, 0, screenSize.width, screenSize.height);

    }

//    public void draw() {
////        clear();
////        axis();
//        for (GObject objs : objectQueue) {
//            for (Runnable r : objs.getDrawQueue()) {
//                r.run();
//            }
//        }
//        frames.add(new Frame(objectQueue));
//    }


    /**
     * Optimization, this method will try to find a GPoint that is at the same coordinates of the given CartesianPoint.
     * If not found, it creates said GPoint.
     * @param target The cartesianPoint.
     * @return A new, or old GPoint.
     */
    public GPoint findOrCreatePoint(CartesianPoint target) {
        Iterator<GObject> iterator = gObjects.iterator();
        while (iterator.hasNext()) {
            GObject obj = iterator.next();
            if (obj instanceof GPoint gp && Objects.equals(gp.getPivot(), target)) {
                iterator.remove(); // Remove from current parent
                return gp;
            }
        }
        return new GPoint(this, target);
    }

    /**
     * Returns the graphics used by the renderer
     * @return Graphics2D
     */
    public Graphics2D getGraphics() {
        return graphics;
    }
}