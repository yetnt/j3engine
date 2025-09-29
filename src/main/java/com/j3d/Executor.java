package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.*;
import com.j3d.engine.geometry.base.CartesianPoint;

import java.awt.*;

/**
 * Executor is a class called by {@link Main#main(String[])} that just draws things ot the window
 */
public class Executor {
    /**
     * The renderer instance.
     */
    private final Renderer renderer;

    /**
     * Default Constructor
     * @param r The Renderer Instance.
     */
    public Executor(Renderer r) {
        renderer = r;
    }

    /**
     * Runs the executor.
     */
    public void run(Graphics2D graphics2D) {

        // draws 3 lines that hopefully connects to a triangle

        renderer.axis(graphics2D);

        cube(graphics2D);
    }

    public void cube(Graphics2D graphics2D) {
        double size = 20.0; // The "side length" of the cube in Cartesian units
        CartesianPoint offset = new CartesianPoint(0, 0); // Center the cube at the origin

        // Define the 6 visible vertices of a standard isometric cube
        GPoint top = renderer.findOrCreatePoint(new CartesianPoint(offset.x, offset.y + size), null);
        GPoint bottom = renderer.findOrCreatePoint(new CartesianPoint(offset.x, offset.y - size), null);
        GPoint leftTop = renderer.findOrCreatePoint(new CartesianPoint(offset.x - size, offset.y + size / 2), null);
        GPoint leftBottom = renderer.findOrCreatePoint(new CartesianPoint(offset.x - size, offset.y - size / 2), null);
        GPoint rightTop = renderer.findOrCreatePoint(new CartesianPoint(offset.x + size, offset.y + size / 2), null);
        GPoint rightBottom = renderer.findOrCreatePoint(new CartesianPoint(offset.x + size, offset.y - size / 2), null);

        // Create the three visible faces of the cube using different shades for a 3D effect
        // Top face (lightest color)
        createFace(leftTop, top, rightTop, new CartesianPoint(offset.x, offset.y), new Color(180, 180, 255));

        // Left face (darkest color)
        createFace(leftBottom, leftTop, top, bottom, new Color(80, 80, 180));

        // Right face (medium color)
        createFace(rightBottom, rightTop, top, bottom, new Color(10, 200, 12));
    }

    /**
     * Helper method to create a quadrilateral face from four points using two GTri objects.
     * Assumes points are given in clockwise or counter-clockwise order.
     *
     * @param p1     First vertex
     * @param p2     Second vertex
     * @param p3     Third vertex
     * @param p4     Fourth vertex
     * @param color  The color of the face
     */
    private void createFace(GPoint p1, GPoint p2, GPoint p3, GPoint p4, Color color) {
        // Create the face with two triangles
        new GTri(color.brighter(), renderer, p1, p2, p3);
        new GTri(color, renderer, p1, p3, p4);
    }

    private void createFace(GPoint p1, GPoint p2, GPoint p3, CartesianPoint p4, Color color) {
        new GTri(color, renderer, p1, p2, p3);
    }
}
