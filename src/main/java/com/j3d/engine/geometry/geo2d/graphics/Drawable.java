package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;

/**
 * Interface that lets use know something can be drawn. this should later be moved into Pure and hence
 */
public interface Drawable {
    /**
     * Draws this geometry to the screen.
     * @param graphics2D The Graphics2D instance
     * @implSpec This is meant to be overridden by child classes.
     */
    void draw(Graphics2D graphics2D);

    /**
     * Draws this geometry to the screen, but in a selected state.
     * @param graphics2D The Graphics2D instance
     * @implSpec This is meant to be overridden by child classes.
     */
    void drawSelected(Graphics2D graphics2D);

    UUID rendererUUID();

    GObject objectParent();

    Vector3 getPivot();
}
