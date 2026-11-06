package com.j3d.engine.scene.nodes.util.tri;

public final class Fetch {
    public final EarClipping earClipping = new EarClipping();
    public final Delaunay delaunay = new Delaunay();
    public final Seidel seidel = new Seidel();

    public EarClipping getEarClipping() {
        return earClipping;
    }

    public Delaunay getDelaunay() {
        return delaunay;
    }

    public Seidel getSeidel() {
        return seidel;
    }
}
