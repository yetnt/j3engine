package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.*;
import com.j3d.engine.geometry.base.CartesianPoint;

import java.awt.*;

public class Executor {
    private Renderer renderer;

    public Executor(Renderer r) {
        renderer = r;
    }

    public void run() {
        GLine line = renderer.line(new CartesianPoint(0, 9), new CartesianPoint(-30.2, 60));
//        renderer.draw();
    }
}
