package com.j3d.engine.geometry;

import java.awt.*;

/**
 * Interface that lets use know something can be drawn.
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
}
