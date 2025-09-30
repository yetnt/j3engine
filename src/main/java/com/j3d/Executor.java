package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.*;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;

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

//        renderer.axis(graphics2D, Main.camera);

        test();
    }

    public void test() {
        GPoint A = new GPoint(new Vector3(-1, -1, 0));
        GPoint B = new GPoint(new Vector3(0, -1, -2));
        GPoint C = new GPoint(new Vector3(-10, 0, -2));
        GTri triangl = new GTri(Color.ORANGE, A, B, C);
        new Thing(renderer, null).addObjs(triangl, triangl.getLegA(), triangl.getLegB(), triangl.getLegC(), A, B, C);
    }
}
