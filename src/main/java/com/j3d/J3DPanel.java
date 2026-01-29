package com.j3d;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.selection.SelectionUI;

import javax.swing.*;

import java.awt.*;

import static com.j3d.J3DSettings.jMenuBarOffsetY;
import static com.j3d.Main.*;

/**
 *  The J3DPanel class extends JPanel and serves as the main drawing surface for the 3D engine.
 * It overrides the `paint` method to handle rendering of 3D objects, axes, and selection UI.
 */
public class J3DPanel extends JPanel {
    public J3DPanel() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (run) {
//            renderer.axis((Graphics2D) g, camera);
            executor.run((Graphics2D) g);
            run = false;
        }
        renderer.draw((Graphics2D) g, camera);
        // draw selection area ontop of all render things.
        if (selectionArea[0] != null && selectionArea[1] != null) {
            int offset = f.getJMenuBar().getSize().height + jMenuBarOffsetY;
            selectionArea[0] = new ScreenPoint(selectionArea[0].x, selectionArea[0].y - offset);
            selectionArea[1] = new ScreenPoint(selectionArea[1].x, selectionArea[1].y - offset);
            SelectionUI.run((Graphics2D)g, selectionArea, renderer);
        }
//        log.println("Painted/Repainted Scene");
    }
}
