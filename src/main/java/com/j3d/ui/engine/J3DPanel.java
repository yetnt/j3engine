package com.j3d.ui.engine;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.selection.SelectionUI;

import javax.swing.*;

import java.awt.*;

import static com.j3d.J3DSettings.jMenuBarOffsetY;
import static com.j3d.ui.engine.EngineFrame.*;
import static com.j3d.ui.engine.EngineFrame.selectionArea;

/**
 *  The J3DPanel class extends JPanel and serves as the main drawing surface for the 3D engine.
 * It overrides the `paint` method to handle rendering of 3D objects, axes, and selection UI.
 */
public class J3DPanel extends JPanel {
    private static ScreenPoint[] selectionAreaOld = new ScreenPoint[]{new ScreenPoint(0, 0), new ScreenPoint(0, 0)};
    public J3DPanel() {
        super();
    }

    /**
     * Applies the JMenuBar offset to the selection square so it line sup with the mouse cursor.
     * Also adheres to the fact that a selection may have not previously changed by storing it statically.
     * @param sA The selection area
     * @return The new adjusted selection area
     * @apiNote      * This is needed because the JMenuBar is not part of the JPanel, but rather the JFrame.
     * So the mouse coordinates are relative to the JFrame, but the drawing is relative to the JPanel.
     */
    public ScreenPoint[] applySelectionAreaOffset(ScreenPoint[] sA) {
        if (selectionAreaOld[0].equals(sA[0]) && selectionAreaOld[1].equals(sA[1])) {
            return selectionAreaOld;
        }
        int offset = Static.mainFrame.getJMenuBar().getSize().height + jMenuBarOffsetY;
        ScreenPoint a = new ScreenPoint(sA[0].x, sA[0].y - offset);
        ScreenPoint b = new ScreenPoint(sA[1].x, sA[1].y - offset);
        selectionAreaOld = new ScreenPoint[] {
                new ScreenPoint(a.x, a.y),
                new ScreenPoint(b.x, b.y)
        };
        return new ScreenPoint[] {
                a, b
        };
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (run) {
//            renderer.axis((Graphics2D) g, camera);
            Static.executor.run((Graphics2D) g);
            run = false;
        }
        Static.renderer.draw((Graphics2D) g, Static.camera);
        // draw selection area ontop of all render things.
        if (selectionArea[0] != null && selectionArea[1] != null) {
            SelectionUI.run((Graphics2D)g, applySelectionAreaOffset(selectionArea), Static.renderer);
        }
    }
}
