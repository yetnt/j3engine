package com.j3d.engine.draw;

import com.j3d.engine.geometry.geo2d.graphics.Drawable;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.pure.Pure;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;

public class RenderState<T extends Pure, R extends GObject> implements Drawable{
    private final R parent;
    private final T pure;
    private UUID id;
    private boolean valid = true;
    private Consumer<Graphics2D> onDraw = (g) -> {};
    private Consumer<Graphics2D> onDrawSelected = (g) -> {};

    public RenderState(T pue, R par) {
        id = par.getId();
        this.pure = pue;
        this.parent = par;
    }

    public RenderState<T, R> invalidate() {
        valid = false;
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public R getParent() {
        return parent;
    }

    public T getPure() {
        return pure;
    }

    public UUID getId() {
        return id;
    }

    public void setConsumers(
        Consumer<Graphics2D> onDraw,
        Consumer<Graphics2D> onDrawSelected
    ) {
        this.onDraw = onDraw;
        this.onDrawSelected = onDrawSelected;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        onDraw.accept(graphics2D);
    }

    @Override
    public void drawSelected(Graphics2D graphics2D) {
        onDrawSelected.accept(graphics2D);
    }

    public Vector3 getPivot() {
        return getPure().getPivot();
    }
}
