package com.j3d;

import com.j3d.engine.interact.selection.SelectionUI;

import javax.swing.*;

import java.awt.*;

import static com.j3d.Main2.*;

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
            SelectionUI.run((Graphics2D)g, selectionArea, renderer);
        }
//        log.println("Painted/Repainted Scene");
    }
}
