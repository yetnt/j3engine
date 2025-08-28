package com.j3d.engine;

import com.j3d.engine.geometry.GLine;
import com.j3d.engine.geometry.GObject;
import com.j3d.engine.geometry.GPoint;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.Dimension;
import com.j3d.engine.geometry.base.ScreenPoint;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

public class Renderer {
    private Graphics2D graphics;
    public Dimension screenSize;
    private ArrayDeque<GObject> objectQueue = new ArrayDeque<>();
    public double SCALE = 10.0;
    public Point cameraOffset = new Point(0, 0);

    public Renderer(Graphics g, Dimension dim) {
        graphics = (Graphics2D) g;
        screenSize = dim;
    }

    public GLine line(CartesianPoint A, CartesianPoint B) {
        GPoint gPointA = findOrCreatePoint(A);
        GPoint gPointB = findOrCreatePoint(B);
        GLine line = new GLine(this, gPointA, gPointB);
        objectQueue.add(line);
        return line;
    }

    public GPoint point(CartesianPoint point) {
        GPoint p = new GPoint(this, point);
        objectQueue.add(p);
        return p;
    }

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

    public GPoint findOrCreatePoint(CartesianPoint target) {
        Iterator<GObject> iterator = objectQueue.iterator();
        while (iterator.hasNext()) {
            GObject obj = iterator.next();
            if (obj instanceof GPoint gp && Objects.equals(gp.getPivot(), target)) {
                iterator.remove(); // Remove from current parent
                return gp;
            }
        }
        return new GPoint(this, target);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }
}