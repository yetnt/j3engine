package com.j3d.engine.geometry.geo2d.pure;

import com.j3d.engine.draw.RenderState;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j3d.StaticRefs.getCamera;
import static com.j3d.StaticRefs.getSceneManager;

public class Segment implements Pure {

    private Vector3 start;
    private Vector3 end;
    private boolean isValid = true;

    public Segment(Vector3 start, Vector3 end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public UUID getId() {
        return UUID.randomUUID();
    }

    @Override
    public Vector3 getPivot() {
        return
                start.add(end).div(2);
    }

    public Vector3 getStart() {
        return start;
    }

    public Vector3 getEnd() {
        return end;
    }

    @Override
    public RenderState<Segment, GObject> toRenderState(GObject parent) {
        Consumer<Graphics2D> swingDraw = (graphics2D) -> {
            graphics2D.drawLine(
                    start.toPoint(getCamera()).toScreen(getSceneManager()).x,
                    start.toPoint(getCamera()).toScreen(getSceneManager()).y,
                    end.toPoint(getCamera()).toScreen(getSceneManager()).x,
                    end.toPoint(getCamera()).toScreen(getSceneManager()).y
            );
        };
        RenderState<Segment, GObject> rs = Pure.super.toRenderState(parent);
        rs.setConsumers(
                (g) -> {
                    if (parent == null) return;
                    if (parent.isDeletedState()) return;
//                            if (getSceneManager().getSelected().contains(parent)) {
//                                drawSelected(graphics2D);return;
//                            }
                    g.setColor(parent.getColour());
                    swingDraw.accept(g);
                },
                (g) -> {
                    if (parent == null) return;
                    if (parent.isDeletedState()) return;
                    g.setColor(parent.getColour().brighter());
                    g.setStroke(new BasicStroke(4));
                    swingDraw.accept(g);
                    g.setStroke(new BasicStroke(1));
                }
        );
        return rs;
    }


    public void swingDraw(Graphics2D graphics2D) {
        graphics2D.drawLine(
                start.toPoint(getCamera()).toScreen(getSceneManager()).x,
                start.toPoint(getCamera()).toScreen(getSceneManager()).y,
                end.toPoint(getCamera()).toScreen(getSceneManager()).x,
                end.toPoint(getCamera()).toScreen(getSceneManager()).y
        );
    }
}
