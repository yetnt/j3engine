package com.j3d.engine.scene.nodes.util.tri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum Triangulation {
    EARCLIPPING(TriangulationMethod.Fetch::getEarClipping, (byte) 1),
    DELAUNAY(TriangulationMethod.Fetch::getDelaunay, (byte) 2),
    SEIDEL(TriangulationMethod.Fetch::getSeidel, (byte) 3);

    private final Supplier<TriangulationMethod> supplier;
    private final byte id;
    Triangulation(Supplier<TriangulationMethod> supplier, byte id) {
        this.supplier = supplier;
        this.id = id;
    }

    public TriangulationMethod getTriangulationMethod() {
        return supplier.get();
    }

    public byte getId() {
        return id;
    }

    @Nullable
    @Contract(pure = true)
    public static Triangulation fromId(byte v) {
        for (Triangulation t : Triangulation.values()) {
            if (t.id == v) {
                return t;
            }
        }
        return null;
    }
}
