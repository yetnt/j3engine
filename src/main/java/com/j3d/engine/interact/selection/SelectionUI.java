package com.j3d.engine.interact.selection;

import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.ui.theme.CursorManager;
import com.j3d.ui.theme.CursorNames;

import java.awt.*;

/**
 * SelectionUI is a utility class for rendering the selection rectangle on the screen and determining the type of selection based on user input.
 * It provides methods to draw the selection rectangle with appropriate colours and cursors, and to infer the selection type (strict, soft, add, subtract)
 * based on the mouse drag direction and keyboard modifiers.
 * @see SelectionManager
 * @see SelectionQuery
 * @see SelectionType
 * @see SelectionMouseOwner
 * @see SelectionUtils
 * @author Lehlogonolo Poole
 */
public class SelectionUI {
    private static final Color STRICT_COLOR = new Color(0, 255, 0, 26);
    private static final Color SOFT_COLOR = new Color(255, 255, 0, 26);
    private static final Color SUBTRACT_COLOR = new Color(255, 0, 0, 26);
    private static final Color ADD_COLOR = new Color(154, 0, 255, 26);

    /**
     * The basic inferred selection via mouse dragging in combination with
     * keybinds.
     */
    public static SelectionUtils.InferredSelectionType inferredSelection = SelectionUtils.InferredSelectionType.NONE;

    public static void run(Graphics2D g, ScreenPoint[] selectionArea, SceneManager sceneManager) {
        boolean isStrict = isStrict(selectionArea); // If the user dragged upwards, it's strict, otherwise it's soft.
        ScreenPoint i = selectionArea[0];
        ScreenPoint ii = selectionArea[1];
//        g.setColor(invert ? SUBTRACT_COLOR : isStrict ? STRICT_COLOR : SOFT_COLOR);
        g.setColor(SelectionUtils.usingSelectionVariant(inferredSelection, isStrict,
                ADD_COLOR, SUBTRACT_COLOR, STRICT_COLOR, SOFT_COLOR));
//        CursorManager.set(invert ? "selectSubtract" : isStrict ? "selectStrict" : "selectSoft");
        CursorManager.set(SelectionUtils.usingSelectionVariant(inferredSelection, isStrict,
                CursorNames.SELECT_ADD, CursorNames.SELECT_SUBTRACT, CursorNames.SELECT_STRICT, CursorNames.SELECT_SOFT));
        g.fillRect(Math.min(i.x, ii.x), Math.min(i.y, ii.y), Math.abs(i.x - ii.x), Math.abs(i.y - ii.y));
        SelectionQuery selectionQuery = new SelectionQuery(
                i, ii,
                SelectionUtils.usingSelectionVariant(inferredSelection, isStrict,
                        SelectionType.UNION, SelectionType.SUBTRACT, SelectionType.BOUNDS_STRICT, SelectionType.BOUNDS_SOFT)
        );
        SelectionManager m = sceneManager.select(selectionQuery);
    }

    public static boolean isStrict(ScreenPoint [] selectionArea) {
        return selectionArea[0].y >= selectionArea[1].y;
    }
}
