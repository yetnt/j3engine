package com.j3d.engine.interact.selection;

import com.j3d.Static;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.ui.CursorManager;

import java.awt.event.MouseEvent;

import static com.j3d.Static.getLog;
import static com.j3d.Static.sceneManager;
import static com.j3d.ui.engine.EngineFrame.*;
import com.j3d.engine.react.events.*;

/**
 * SelectionMouseOwner is a MouseOwner which is responsible for handling mouse events related to
 * selection in the sceneManager. It allows the user to click and drag to create a selection square on
 * the screen, which can be used to select multiple objects in the sceneManager. It also allows the user
 * to click to clear the selection square and broadcast an event to clear the selection in the
 * {@link SceneManager}
 * @see SelectionManager
 * @see SelectionQuery
 * @see SelectionType
 * @see SelectionUI
 * @see SelectionUtils
 * @author Lehlogonolo Poole
 */
public class SelectionMouseOwner extends MouseOwner {
    public SelectionMouseOwner() {
        super(MOwner.SELECTION);
    }

    /**
     * Clears the selection square drawn on the screen and resets the mouse position.
     */
    public void clearSelectionSquare() {
        selectionArea = new ScreenPoint[]{null, null};
        Static.mainFrame.repaint();
        CursorManager.setDefault();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isNotOwner()) return;
        clearSelectionSquare();
        broadcast(EventType.X_SELECTED, new EventPayload<>(this, Static.sceneManager) {
        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        mousePos = new ScreenPoint(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
        mousePos = null;
        if (selectionArea[0] != null && selectionArea[1] != null) {
            Static.getLog().println("Final Selection Area: " + selectionArea[0] + " to " + selectionArea[1]);
            getLog().println("Selected " + sceneManager.getSelected().size() + " objects.");
        }
    }

    @Override
    public void mouseDraggedUsingClickDelay(MouseEvent e) {
        if (isNotOwner()) return;
        selectionArea[0] = mousePos;
        selectionArea[1] = new ScreenPoint(e.getX(), e.getY());
        Static.mainFrame.repaint();
    }
}
