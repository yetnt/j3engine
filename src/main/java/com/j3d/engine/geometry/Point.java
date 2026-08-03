package com.j3d.engine.geometry;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.ViewType;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.math.matrix.Vector3;

import java.awt.*;
import java.util.UUID;

import static com.j3d.engine.scene.nodes.geometry.GPoint.DIAMETER;

public class Point extends Vector3 implements Pure {
    private boolean isValid = true;

    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    public static Point from(Vector3 v) {
        return new Point(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public UUID getId() {
        return UUID.randomUUID();
    }

    @Override
    public Vector3 getPivot() {
        return this;
    }

    @Override
    public RenderState<Point, GObject> toRenderState(GObject parent) {
        GPoint point = (GPoint) parent;
        RenderState<Point, GObject> rs = Pure.super.toRenderState(parent);
        rs.setConsumers(
                (g) -> {
                    if (StaticConfig.getViewType() != ViewType.WIREFRAME)
                        if (point.hasParent())
                            return;
                    g.setColor(point.getColour());
                    ScreenPoint p = getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager());
                    g.fillOval(p.x - DIAMETER / 2, p.y - DIAMETER / 2, DIAMETER, DIAMETER);
                },
                (g) -> {
                    if (StaticConfig.getViewType() != ViewType.WIREFRAME)
                        if (point.hasParent() /*&& getParents().stream().findAny().get().hasParent() */) {
                            return;
                        }
                    g.setColor(Color.WHITE);
                    ScreenPoint p = this.getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager());
                    g.fillOval(p.x - (DIAMETER+1) / 2, p.y - (DIAMETER+1) / 2, (DIAMETER+1), (DIAMETER+1));
                }
        );
        return rs;
    }
}
