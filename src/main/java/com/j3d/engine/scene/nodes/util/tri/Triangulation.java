/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d.engine.scene.nodes.util.tri;

import com.j3d.engine.math.matrix.Vector3;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author yetnt
 */
public abstract sealed class Triangulation permits EarClipping {
    
    public static final EarClipping earClipping = new EarClipping();
    
    // in the event a polygon wants to flip the order/direciton of connected lines
    // not to be confused with the renderer's segment.
    static record Segment(
        Vector3 A,
        Vector3 B
    ) {}
    
    static record Triangulated(
        ArrayList<Vector3> newPoints, // sometimes empty (EatClipping creates no new points)
        ArrayList<Segment> segments
    ) {}
    
    @Nullable
    public abstract Triangulated apply(@NotNull ArrayList<Vector3> v3s);
}
