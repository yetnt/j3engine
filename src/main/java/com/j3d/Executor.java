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

//        test();
        Thing cub = cube();
        cub.translate(new Vector3(0, 20, 0));
    }

    public void test() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(0, 10, 0));
        GPoint C = new GPoint(new Vector3(0, 0, 10));
        GTri triangl = new GTri(Color.ORANGE, A, B, C);
        new Thing(renderer, null).addObjs(triangl, triangl.getLegA(), triangl.getLegB(), triangl.getLegC(), A, B, C);
    }

    public Thing cube() {
        GPoint A = new GPoint(new Vector3(-5, 5, -5));
        GPoint B = new GPoint(new Vector3(-5, -5, -5));
        GPoint C = new GPoint(new Vector3(5, 5, -5));
        GTri face1tri1 = new GTri(Color.ORANGE, A, B, C);
        GPoint D = new GPoint(new Vector3(5, -5, -5));
        GTri face1tri2 = new GTri(Color.ORANGE.darker(), B, C, D);
        GPoint E = new GPoint(new Vector3(5, 5, 5));
        GTri face2tri1 = new GTri(Color.PINK, D, C, E);
        GPoint F = new GPoint(new Vector3(5, -5, 5));
        GTri face2tri2 = new GTri(Color.PINK.darker(), D, E, F);
        GPoint H = new GPoint(new Vector3(-5, 5, 5));
        GTri face3tri1 = new GTri(Color.GREEN, F, E, H);
        GPoint G = new GPoint(new Vector3(-5, -5, 5));
        GTri face3tri2 = new GTri(Color.GREEN.darker(), F, H, G);
        GTri face4tri1 = new GTri(Color.BLUE, H, A, G);
        GTri face4tri2 = new GTri(Color.BLUE.darker(), A, G, B);
        GTri face5tri1 = new GTri(Color.LIGHT_GRAY, A, H, C);
        GTri face5tri2 = new GTri(Color.LIGHT_GRAY.darker(), H, C, E);
        GTri face6tri1 = new GTri(Color.YELLOW, D, F, B);
        GTri face6tri2 = new GTri(Color.YELLOW.darker(), F, B, G);

        return new Thing(renderer, null).addObjs(
                A, B, C, D, E, F, G, H, face1tri1, face1tri2, face2tri1, face2tri2, face3tri1, face3tri2, face4tri1, face4tri2,
                face5tri1, face5tri2, face6tri1, face6tri2
        );
    }
}
