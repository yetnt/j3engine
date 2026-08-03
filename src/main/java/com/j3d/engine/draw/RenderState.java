package com.j3d.engine.draw;

import com.j3d.engine.geometry.geo2d.graphics.Drawable;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.pure.Pure;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * Represents the rendering state for a graphical object, linking a pure geometric definition
 * with its graphical object representation. It manages drawing logic and selection state.
 *
 * @param <P> The type of the pure geometric object, extending {@link Pure}.
 * @param <O> The type of the graphical object, extending {@link GObject}.
 */
public class RenderState<P extends Pure, O extends GObject> implements Drawable {
    /**
     * The graphical object instance that this render state belongs to.
     */
    private final O parent;
    /**
     * The pure geometric definition associated with this render state.
     */
    private final P pure;
    private final UUID id;
    /**
     * Whether this render state is valid or not.
     */
    private boolean valid = true;
    private Consumer<Graphics2D> onDraw = (g) -> {};
    private Consumer<Graphics2D> onDrawSelected = (g) -> {};

    public RenderState(P pureInstance, O parentInstance) {
        id = parentInstance.getId();
        this.pure = pureInstance;
        this.parent = parentInstance;
    }

    /**
     * Marks this render state as invalid. An invalid render state
     * typically means it should no longer be used or rendered.
     */
    public void invalidate() {
        valid = false;
    }

    /**
     * Checks if this render state is currently valid. An invalid render state
     * typically means it should no longer be used or rendered.
     *
     * @return {@code true} if the render state is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    public O getParent() {
        return parent;
    }

    public P getPure() {
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
