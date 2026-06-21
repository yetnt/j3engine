package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.util.stream.Stream;

public class CTri extends CObject {
    public final CLine LegA;
    public final CLine LegB;
    public final CLine LegC;

    public Vector3 normal;

    public CTri(GTri gTri) {
        super(gTri);
        LegA = gTri.getLegA() != null ? gTri.getLegA().toConstraintObject() : null;
        LegB = gTri.getLegB() != null ? gTri.getLegB().toConstraintObject() : null;
        LegC = gTri.getLegC() != null ? gTri.getLegC().toConstraintObject() : null;
    }

    /**
     * Calculates the depth of the tri relative to the camera's forward direction.
     * @return The depth value.
     */
    public double calcDepth() {
        Vector3 toTri = getPivot().sub(Static.camera.getPosition());
        return toTri.dot(Static.camera.getForward().normalize());
    }

    /**
     * Calculates the Euclidean distance from the triangle's pivot to the camera position.
     * @return The Euclidean distance.
     */
    public double euclideanDist() {
        return getPivot().sub(Static.camera.getPosition()).magnitude();
    }


    /**
     * Calculates the normal vector of the triangle given three vertices.
     * @param A The first vertex of the triangle.
     * @param B The second vertex of the triangle.
     * @param C The third vertex of the triangle.
     */
    public void calcNormal(Vector3 A, Vector3 B, Vector3 C) {
        Vector3 AB = B.sub(A);
        Vector3 AC = C.sub(A);
        normal = AB.cross(AC).normalize();
    }

    /**
     * Gets Leg A
     * @return GLine
     */
    public CLine getLegA() {
        return LegA;
    }

    /**
     * Gets Leg B
     * @return GLine
     */
    public CLine getLegB() {
        return LegB;
    }

    /**
     * Gets Leg C
     * @return GLine
     */
    public CLine getLegC() {
        return LegC;
    }

    /**
     * Calculates the area of the triangle.
     * @return The area.
     */
    public double area() {
        Vector3 A = LegA.getStart().getPivot();
        Vector3 B = LegB.getStart().getPivot();
        Vector3 C = LegC.getStart().getPivot();

        return Math.abs((B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX())) / 2;
    }

    public Stream<CLine> getLegStream() {
        return Stream.of(LegA, LegB, LegC);
    }
}
