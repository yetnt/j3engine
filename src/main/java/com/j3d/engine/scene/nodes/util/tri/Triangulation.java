/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d.engine.scene.nodes.util.tri;

import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import java.util.ArrayList;

import com.j3d.engine.scene.nodes.geometry.GPoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author yetnt
 */
public abstract sealed class Triangulation permits EarClipping {

    public static final Fetch Fetch = new Fetch();

    public record Tri(GPoint A, GPoint B, GPoint C) {

        @NotNull
        @Contract(" -> new")
        public Tri flip() {
            return new Tri(C, B, A);
        }

        @NotNull
        @Contract(" -> new")
        public Triangle toTriangle() {
            return new Triangle(A.toPoint(), B.toPoint(), C.toPoint());
        }
    }

    public record Triangulated(
            ArrayList<Vector3> newPoints,
            ArrayList<Tri> tris
    ) {}

    @Nullable
    public abstract Triangulated apply(@NotNull ArrayList<GPoint> v3s);
}
