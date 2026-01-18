package com.j3d.engine.interact.selection;

import com.j3d.J3DSettings;
import com.j3d.Main;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;

import java.awt.event.MouseEvent;

import static com.j3d.Main.*;

public class SelectionMouseOwner extends MouseOwner {
    public SelectionMouseOwner() {
        super(MOwner.SELECTION);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isNotOwner()) return;
        selectionArea = new ScreenPoint[]{null, null}; // Reset selection area
        f.repaint();
        Main.Cursors.setDefault();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        mousePos = new ScreenPoint(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        mousePos = null;
        if (selectionArea[0] != null && selectionArea[1] != null)
            J3DSettings.log.println("Final Selection Area: " + selectionArea[0] + " to " + selectionArea[1]);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isNotOwner()) return;
        selectionArea[0] = mousePos;
        selectionArea[1] = new ScreenPoint(e.getX(), e.getY());
        f.repaint();
    }
}
