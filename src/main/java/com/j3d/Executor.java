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
    private Renderer renderer;

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
    public void run() {
        GLine line = renderer.line(new CartesianPoint(0, 9), new CartesianPoint(-30.2, 60));
//        renderer.draw();
    }
}
