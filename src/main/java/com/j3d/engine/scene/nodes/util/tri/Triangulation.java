package com.j3d.engine.scene.nodes.util.tri;

import java.util.function.Supplier;

public enum Triangulation {
    EARCLIPPING(TriangulationMethod.Fetch::getEarClipping),
    DELAUNAY(TriangulationMethod.Fetch::getDelaunay),
    SEIDEL(TriangulationMethod.Fetch::getSeidel);

    private final Supplier<TriangulationMethod> supplier;
    Triangulation(Supplier<TriangulationMethod> supplier) {
        this.supplier = supplier;
    }

    public TriangulationMethod getTriangulationMethod() {
        return supplier.get();
    }
}
