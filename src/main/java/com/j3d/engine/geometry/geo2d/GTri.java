package com.j3d.engine.geometry.geo2d;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.react.events.EventBroadcast;
import com.j3d.engine.react.events.EventEmitter;

import java.awt.*;
import java.util.*;

import com.j3d.engine.geometry.geo3d.Vector3;

/**
 * GTri represents a Triangle. What'd you expect kau.
 */
public class GTri extends GObject{
    /**
     * Leg A, connected to Leg B and Leg C
     */
    private final GLine LegA;
    /**
     * Leg B, connected to Leg A and Leg C
     */
    private final GLine LegB;
    /**
     * Leg C, connected to Leg A and Leg B
     */
    private final GLine LegC;

    /**
     * The normal of the triangle.
     */
    public Vector3 normal;

    private boolean hidden = false;

    @Override
    public void draw(Graphics2D graphics2D) {
        setPivot(LegA.getStart().getPivot().add(LegB.getStart().getPivot()).add(LegC.getStart().getPivot()).div(3));
        calcNormal(LegA.getStart().getPivot(), LegB.getStart().getPivot(), LegC.getStart().getPivot());
        if (J3DSettings.getViewType() == ViewType.NORMAL) {
            graphics2D.setColor(col);
            graphics2D.fillPolygon(new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x
                    },
                    new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y
                    },
                    3
            );
        }
        // dispatch to lines
        LegA.draw(graphics2D);
        LegB.draw(graphics2D);
        LegC.draw(graphics2D);
    }

    private void drawDist() {
                Static.renderer.scheduleOverlap(g -> {
                            if (J3DSettings.isShowTriDistances()) {
                                // draw text showing the tris distance from camera
                                Vector3 triCentroid = this.getPivot();
                                Static.renderer.drawText3D(g, triCentroid,
                                        String.format("Dist: %.2f", this.getPivot().sub(Static.camera.getPosition()).magnitude()),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                            }
                            if (J3DSettings.isShowDepth()) {
                                // draw text showing the tris depth from camera
                                Vector3 triCentroid = this.getPivot();
                                double depth = this.calcDepth();
                                Static.renderer.drawText3D(g, triCentroid.add(new Vector3(1, 0, 0)),
                                        String.format("Depth: %.2f", depth),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                            }
                            if (J3DSettings.isShowNormals()) {
                                // draw text showing the tris normal
                                Vector3 triCentroid = this.getPivot();
                                Static.renderer.drawText3D(g, triCentroid.sub(new Vector3(4, 0, 0)),
                                        String.format("Normal: (%.2f, %.2f, %.2f)", normal.getX(), normal.getY(), normal.getZ()),
                                        Static.camera,
                                        new Color(0, 0, 0),
                                        this.getColour());
                                // The following code draws the normal
                                g.setColor(Color.RED);
                                Static.renderer.drawLine3D(g, getPivot(), getPivot().add(normal.mult(0.5)), Static.camera);
                            }
                        }
                );
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
     * @return
     */
    public double euclideanDist() {
        return getPivot().sub(Static.camera.getPosition()).magnitude();
    }

    @Override
    public void drawSelected(Graphics2D graphics2D) {
        setPivot(LegA.getStart().getPivot().add(LegB.getStart().getPivot()).add(LegC.getStart().getPivot()).div(3));
        calcNormal(LegA.getStart().getPivot(), LegB.getStart().getPivot(), LegC.getStart().getPivot());
        if (J3DSettings.getViewType() == ViewType.NORMAL) {
            graphics2D.setColor(col.brighter());
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.fillPolygon(new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).x
                    },
                    new int[]{
                            LegA.getStart().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegA.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y,
                            LegB.getEnd().getPivot().toPoint(Static.camera).toScreen(Static.renderer).y
                    },
                    3
            );
            graphics2D.setStroke(new BasicStroke(1));
            draw(graphics2D);
            // dispatch to lines
        }
        LegA.drawSelected(graphics2D);
        LegB.drawSelected(graphics2D);
        LegC.drawSelected(graphics2D);
        Static.renderer.drawText3D(graphics2D, getPivot().sub(new Vector3(1, 1, 1)), "Triangle - " + getId(), Static.camera);
    }

    /**
     * Constructs a new GTri from 3 points.
     *
     * @param c The colour
     * @param A Point A
     * @param B Point B
     * @param C Point C
     */
    public GTri(Color c, GPoint A, GPoint B, GPoint C) {
        super(c);

        LegA = new GLine(A, B);
        LegB = new GLine(B, C);
        LegC = new GLine(C, A);

        setPivot(A.getPivot().add(B.getPivot()).add(C.getPivot()).div(3));

        calcNormal(A.getPivot(), B.getPivot(), C.getPivot());

        TriStateArea.register(this);
        drawDist();
    }

    public GTri(Color c, GLine A, GLine B, GLine C) {
        super(c);
        Vector3[] points = {
                A.getStart().getPivot(), A.getEnd().getPivot(),
                B.getStart().getPivot(), B.getEnd().getPivot(),
                C.getStart().getPivot(), C.getEnd().getPivot()
        };

        // Count how many times each unique point appears
        Map<Vector3, Integer> pointCount = new HashMap<>();
        for (Vector3 p : points) {
            pointCount.merge(p, 1, Integer::sum);
        }

        // A valid triangle should have exactly 3 unique points, each appearing twice
        if (pointCount.size() != 3 || pointCount.values().stream().anyMatch(count -> count != 2)) {
            throw new IllegalArgumentException("Lines do not form a closed triangle.");
        }

        LegA = A;
        LegB = B;
        LegC = C;

        setPivot(A.getStart().getPivot().add(B.getStart().getPivot()).add(C.getStart().getPivot()).div(3));

        calcNormal(A.getStart().getPivot(), B.getStart().getPivot(), C.getStart().getPivot());
        TriStateArea.register(this);
        drawDist();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static class Event extends EventBroadcast {

        public GLine[] LegA = new GLine[2];
        public GLine[] LegB = new GLine[2];
        public GLine[] LegC = new GLine[2];

        /**
         * Default Constructor for EventBroadcast
         *
         * @param e The initiator of the broadcast.
         * @param r The Renderer instance.
         */
        public Event(EventEmitter e, Renderer r) {
            super(e, r);
        }
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


    public GLine getLegA() {
        return LegA;
    }

    public GLine getLegB() {
        return LegB;
    }

    public GLine getLegC() {
        return LegC;
    }

    public double area() {
        Vector3 A = LegA.getStart().getPivot();
        Vector3 B = LegB.getStart().getPivot();
        Vector3 C = LegC.getStart().getPivot();

        return Math.abs((B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX())) / 2;
    }

    @Override
    public String toString() {
        return col.toString() + " GTri";
    }

    @Override
    public boolean deleteSelf() {
        TriStateArea.unregister(this);
        LegA.deleteSelf();
        LegB.deleteSelf();
        LegC.deleteSelf();
        return true;
    }
}
