package com.j3d.engine.scene.draw;

import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.ui.engine.EngineFrame;

import java.awt.*;

// TODO: Refactor from EngineFrame into the Settings Object enum. while staying in the JMenuBar
/**
 * An enum describing how a {@link GTri} should draw itself. This just changes whether a triangle will draw
 * it's legs, points or its area. This can be changed via the JMenuBar
 * within {@link EngineFrame}
 * @author Lehlogonolo Poole
 * @see GTri#draw(Graphics2D)
 * @see GLine#draw(Graphics2D)
 * @see GPoint#draw(Graphics2D)
 * @see EngineFrame
 */
public enum ViewType {
    /**
     * Draw the triangle's area and its edges only.
     */
    NORMAL,
    /**
     * Draw the triangle's lines and points only.
     */
    WIREFRAME
}
