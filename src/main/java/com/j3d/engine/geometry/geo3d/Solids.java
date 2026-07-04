package com.j3d.engine.geometry.geo3d;

import com.j3d.Static;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.Winding;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.layer.Layer;
import com.j3d.utility.generic.SamePair;

import java.awt.*;
import java.util.ArrayList;
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
    public static void topFaceTri(ArrayList<GLine> lines, GPoint p1, GPoint p2, GPoint centre, ArrayList<GTri> tris) {
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
     * @param parentLayer The layer to which this prism will be added in the scene.
     * @param centres A {@link SamePair} containing the {@link Vector3} coordinates for the bottom and top centers of the prism.
     * @param planes A {@link SamePair} containing the {@link Plane} objects defining the bottom and top planes of the prism.
     * @return A {@link Thing} object representing the created prism.
     * <p>
     * This method generates a prism by creating two n-gons (defined by `sideFaceAmts` and `radius`)
     * on the specified bottom and top planes, and then connecting their corresponding vertices to form the side faces.
     */

    public static Thing prism(int radius, int sideFaceAmts, Layer parentLayer, SamePair<Vector3> centres, SamePair<Plane> planes) {
        Vector3 bottomCentre = centres.first;
        Vector3 topCentre = centres.second;
        Plane bottomPlane = planes.first;
        Plane topPlane = planes.second;

        GPoint centre = new GPoint(bottomCentre);
        GPoint centre2 = new GPoint(topCentre);

        Random random = new Random();
        ArrayList<GPoint> points = Sampler.ngon(
                centre.getPivot(),
                radius,
                bottomPlane,
                sideFaceAmts,
                p -> {
                    GPoint point = new GPoint(p);
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
                centre2.getPivot(),
                radius,
                topPlane,
                sideFaceAmts,
                p -> {
                    GPoint point = new GPoint(p);
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
        ArrayList<GLine> lines = new ArrayList<>();
        ArrayList<GTri> tris = new ArrayList<>();
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
            Color col = A.getColour();
            lines.addAll(
                    List.of(
                            AB, BC, CD, DA, diagonalBD
                    )
            );
            tris.add(new GTri(col, AB, diagonalBD, DA, new Winding(D, B, A)));
            tris.add(new GTri(col, BC, diagonalBD, CD, new Winding(B, D, C)));
        }

        Thing circleThing = new Thing(Static.sceneManager, parentLayer, "Prism-"+radius);
        circleThing.addObjs(centre, centre2)
                .addObjs(points.toArray(new GPoint[0]))
                .addObjs(points2.toArray(new GPoint[0]))
                .addObjs(tris.toArray(new GTri[0]))
                .addObjs(lines.toArray(new GLine[0]))
                .solidify();

        return circleThing;
    }
}
