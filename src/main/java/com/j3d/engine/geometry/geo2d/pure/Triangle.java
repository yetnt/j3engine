package com.j3d.engine.geometry.geo2d.pure;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.draw.RenderState;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import static com.j3d.StaticRefs.*;

public class Triangle implements Pure {

    private Vector3 p1;
    private Vector3 p2;
    private Vector3 p3;
    private boolean isValid = true;

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public double area() {
        return area(p1, p2, p3);
    }
    public static double area(Vector3 A, Vector3 B, Vector3 C) {
        return
                B.sub(A).cross(C.sub(A)).magnitude() / 2;
    }

    @Override
    public UUID getId() {
        return UUID.randomUUID();
    }

    @Override
    public Vector3 getPivot() {
        return Vector3.reduceToVector3(List.of(p1, p2, p3), Vector3::add).div(3);
    }

    public Vector3 getP1() {
        return p1;
    }

    public Vector3 getP2() {
        return p2;
    }

    public Vector3 getP3() {
        return p3;
    }

    @Override
    public RenderState<Triangle, GObject> toRenderState(GObject parent) {
        RenderState<Triangle, GObject> rs = Pure.super.toRenderState(parent);
        rs.setConsumers(
                (g) -> {
                    if (parent.isDeletedState()) return;
                    if (StaticConfig.getViewType() == ViewType.NORMAL) {
                        g.setColor(parent.getColour());
                        g.fillPolygon(new int[]{
                                        p1.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x,
                                        p2.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x,
                                        p3.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x
                                },
                                new int[]{
                                        p1.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y,
                                        p2.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y,
                                        p3.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y
                                },
                                3
                        );
                    }
                },
                (g) -> {
                    if (StaticConfig.getViewType() == ViewType.NORMAL) {
                        g.setColor(parent.getColour().brighter());
                        g.setStroke(new BasicStroke(2));
                        g.fillPolygon(new int[]{
                                        p1.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x,
                                        p2.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x,
                                        p3.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).x
                                },
                                new int[]{
                                        p1.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y,
                                        p2.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y,
                                        p3.toPoint(StaticRefs.getCamera()).toScreen(getSceneManager()).y
                                },
                                3
                        );
                        g.setStroke(new BasicStroke(1));
                    }
                    getSceneManager().drawText3D(
                            g,
                            getPivot().sub(Vector3.UNIT),
                            "Tri-" + parent.getId().toString().substring(0, 4),
                            StaticRefs.getCamera());
                }
        );
        return rs;
    }
}
