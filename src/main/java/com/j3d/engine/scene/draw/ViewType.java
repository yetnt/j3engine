package com.j3d.engine.scene.draw;

import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.ui.engine.EngineFrame;

import java.awt.*;
/**
 * An enum describing how a {@link GTri} should draw itself. This just changes whether a triangle will draw
 * it's legs, points or its area. This can be changed via the JMenuBar
 * within {@link EngineFrame}
 * @author Lehlogonolo Poole
 * @see com.j3d.engine.geometry.Drawable#draw(Graphics2D) 
 * @see com.j3d.engine.geometry.Drawable#drawSelected(Graphics2D)
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
