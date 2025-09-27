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
        // x = 1 line

        // draw the x = y line

        GTri t = new GTri(
                renderer,
                new GPoint(renderer, new CartesianPoint(0, 0)),
                new GPoint(renderer, new CartesianPoint(20, 10)),
                new GPoint(renderer, new CartesianPoint(15, -20))
        );

        // draws 3 lines that hopefully connects to a triangle

        renderer.axis(graphics2D);
    }
}
