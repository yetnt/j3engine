package com.j3d.engine.scene.nodes.util;

import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.utility.generic.tuple.SamePair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Solids is a utility class for generating common 3D geometric shapes (solids)
 * composed of {@link GPoint}, {@link GLine}, and {@link GTri} objects.
 * <p>
 * All generated solids are returned as {@link Thing} objects, which can then
 * be added to a {@link SceneManager} for rendering and interaction.
 * </p>
 * @author Lehlogonolo Poole
 * @see Thing
 * @see GPoint
 * @see GLine
 * @see GTri
 * @see Sampler
 */
public class Solids {
    /**
     * Creates a triangle for the top or bottom face of a prism.
     *
     * @param lines An {@link ArrayList} of {@link GLine} objects to which the new lines forming the triangle will be added.
     * @param p1 The first {@link GPoint} vertex of the triangle.
     * @param p2 The second {@link GPoint} vertex of the triangle.
     * @param centre The center {@link GPoint} of the face, which serves as the third vertex of the triangle.
     * @param tris An {@link ArrayList} of {@link GTri} objects to which the newly created triangle will be added.
     *             The triangle is formed by `p1`, `p2`, and `centre`.
     */
    public static void topFaceTri(HashSet<GLine> lines, GPoint p1, GPoint p2, GPoint centre, HashSet<GTri> tris) {
        GLine lnInCirc = GLine.getInstance(lines, p1, p2); // the line that is part of the ngon
        GLine lineA = GLine.getInstance(lines, p1, centre);
        GLine lineB = GLine.getInstance(lines, p2, centre);
        lines.add(lnInCirc);
        lines.add(lineA);
        lines.add(lineB);
        GTri tri = new GTri(
                Color.GRAY,
                lnInCirc,
                lineA,
                lineB,
                new Winding(p1, centre, p2)
        );
        tris.add(tri);
    }

    /**
     * Creates a 3D prism object.
     *
     * @param radius The radius of the prism's base.
     * @param sideFaceAmts The number of sides for the prism's base (e.g., 3 for a triangular prism, 4 for a square prism).
     * @param planes `SamePair` of {@link AxisPlane} objects, where the first plane defines the bottom face and the second defines the top face.
     * <p>
     * This method generates a prism by creating two n-gons (defined by `sideFaceAmts` and `radius`)
     * on the specified bottom and top planes, and then connecting their corresponding vertices to form the side faces.
     */
    public static ArrayList<GObject> prism(double radius, int sideFaceAmts, SamePair<AxisPlane> planes, boolean randCol) {
        Vector3 bottomCentre = planes.first.origin();
        Vector3 topCentre = planes.second.origin();
        AxisPlane bottomAxisPlane = planes.first;
        AxisPlane topAxisPlane = planes.second;

        GPoint centre = new GPoint(bottomCentre);
        GPoint centre2 = new GPoint(topCentre);

        Random random = new Random();
        ArrayList<GPoint> points = Sampler.ngon(
                radius,
                bottomAxisPlane,
                sideFaceAmts,
                p -> {
                    GPoint point = new GPoint(p);
                    if (!randCol) return point;
                    // Random colour
                    int red = random.nextInt(256);   // 0 to 255
                    int green = random.nextInt(256);
                    int blue = random.nextInt(256);
                    //
                    Color randomColor = new Color(red, green, blue);
                    point.setColour(randomColor);
                    return point;
                }
        );
        ArrayList<GPoint> points2 = Sampler.ngon(
                radius,
                topAxisPlane,
                sideFaceAmts,
                p -> {
                    GPoint point = new GPoint(p);
                    if (!randCol) return point;
                    // Random colour
                    int red = random.nextInt(256);   // 0 to 255
                    int green = random.nextInt(256);
                    int blue = random.nextInt(256);
                    //
                    Color randomColor = new Color(red, green, blue);
                    point.setColour(randomColor);
                    return point;
                }
        );
        HashSet<GLine> lines = new HashSet<>();
        HashSet<GTri> tris = new HashSet<>();
        // connect lines to circle
        for (int i = 0; i < points.size(); i++) {
            //bottom face points
            GPoint A = points.get(i);
            GPoint B = points.get((i + 1) % points.size()); // Connect last point to first
            topFaceTri(lines, B, A, centre, tris);
            //top face points
            GPoint D = points2.get(i);
            GPoint C = points2.get((i + 1) % points.size()); // Connect last point to first
            topFaceTri(lines, D, C, centre2, tris);
            // edge lines
            GLine AB = GLine.getInstance(lines, A, B);
            GLine BC = GLine.getInstance(lines, B, C);
            GLine CD = GLine.getInstance(lines, C, D);
            GLine DA = GLine.getInstance(lines, D, A);
            GLine diagonalBD = new GLine(B, D);
            diagonalBD.setColour(
                    new Color(0, 0, 0, 40)
            );

            // Triangle ABD and CDB
            Color col = randCol ? A.getColour() : Color.GRAY;
            lines.addAll(
                    List.of(
                            AB, BC, CD, DA, diagonalBD
                    )
            );
            tris.add(new GTri(col, AB, diagonalBD, DA, new Winding(D, B, A)));
            tris.add(new GTri(col, BC, diagonalBD, CD, new Winding(B, D, C)));
        }

        ArrayList<GObject> all = new ArrayList<>();
        all.add(centre);
        all.add(centre2);
        all.addAll(points);
        all.addAll(points2);
        all.addAll(lines);
        all.addAll(tris);
        return all;
    }

    /**
     * Creates a 3D prism object.
     *
     * @param radius The radius of the prism's base.
     * @param sideFaceAmts The number of sides for the prism's base (e.g., 3 for a triangular prism, 4 for a square prism).
     * @param parentLayer The layer to which this prism will be added in the scene.
     * @param planes `SamePair` of {@link AxisPlane} objects, where the first plane defines the bottom face and the second defines the top face.
     * <p>
     * This method generates a prism by creating two n-gons (defined by `sideFaceAmts` and `radius`)
     * on the specified bottom and top planes, and then connecting their corresponding vertices to form the side faces.
     */

    public static Thing prism(double radius, int sideFaceAmts, Layer parentLayer, SamePair<AxisPlane> planes) {

        ArrayList<GObject> o = prism(radius, sideFaceAmts, planes, true);
        return new Thing(parentLayer, "Prism-"+radius)
                .addObjs(o.toArray(new GObject[0]))
                .solidify();
    }
}
