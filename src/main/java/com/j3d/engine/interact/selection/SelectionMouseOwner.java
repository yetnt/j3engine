package com.j3d.engine.interact.selection;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.ui.engine.floating.properties.PropertiesPanel;
import com.j3d.ui.generic.CursorManager;

import java.awt.event.MouseEvent;

import static com.j3d.StaticRefs.getLog;
import static com.j3d.StaticRefs.sceneManager;
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
        StaticRefs.mainFrame.repaint();
        CursorManager.setDefault();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isNotOwner()) return;
        clearSelectionSquare();
        broadcast(EventType.X_SELECTED, new EventPayload<>(this, StaticRefs.sceneManager) {
        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        mousePos = getSelectionMouseLoc(e);
//        mousePos = new ScreenPoint(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
        mousePos = null;
        if (selectionArea[0] != null && selectionArea[1] != null) {
            StaticRefs.getLog().println("Final Selection Area: " + selectionArea[0] + " to " + selectionArea[1]);
            getLog().println("Selected " + sceneManager.getSelected().size() + " objects.");
            PropertiesPanel.propertiesPanel();
        }
    }

    @Override
    public void mouseDraggedUsingClickDelay(MouseEvent e) {
        if (isNotOwner()) return;
        selectionArea[0] = mousePos;
        selectionArea[1] = getSelectionMouseLoc(e);
//        selectionArea[1] = new ScreenPoint(e.getX(), e.getY());
        StaticRefs.mainFrame.repaint();
    }

}
