package com.j3d.engine.geometry.geo2d.graphics.pure;

import com.j3d.engine.geometry.geo2d.DecomposeWhenDrawn;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;

import static com.j3d.StaticRefs.getCamera;
import static com.j3d.StaticRefs.getSceneManager;

public class Segment<T extends GObject> implements Pure {

    private Vector3 start;
    private Vector3 end;
    private T parent;
    private UUID id = UUID.randomUUID();

    public Segment(Vector3 start, Vector3 end, T parent) {
        this.start = start;
        this.end = end;
        this.parent = parent;
    }

    @Override
    public Vector3 getPivot() {
        return
                start.add(end).div(2);
    }

    @Override
    public GObject objectParent() {
        return parent;
    }

    @Override
    public UUID rendererUUID() {
        return id;
    }

    public Vector3 getStart() {
        return start;
    }

    public Vector3 getEnd() {
        return end;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        if (parent.isDeletedState()) return;
        if (getSceneManager().getSelected().contains(parent)) {
            drawSelected(graphics2D);return;
        }
        graphics2D.setColor(parent.getColour());
        swingDraw(graphics2D);
    }

    public void swingDraw(Graphics2D graphics2D) {
        graphics2D.drawLine(
                start.toPoint(getCamera()).toScreen(getSceneManager()).x,
                start.toPoint(getCamera()).toScreen(getSceneManager()).y,
                end.toPoint(getCamera()).toScreen(getSceneManager()).x,
                end.toPoint(getCamera()).toScreen(getSceneManager()).y
        );
    }

    @Override
    public void drawSelected(Graphics2D graphics2D) {
        if (parent.isDeletedState()) return;
        graphics2D.setColor(parent.getColour().brighter());
        graphics2D.setStroke(new BasicStroke(4));
        swingDraw(graphics2D);
        graphics2D.setStroke(new BasicStroke(1));
    }
}
