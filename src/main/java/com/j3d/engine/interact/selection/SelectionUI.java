package com.j3d.engine.interact.selection;

import com.j3d.Main;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.ScreenPoint;

import java.awt.*;

import static com.j3d.J3DSettings.log;

public class SelectionUI {
    private static final Color STRICT_COLOR = new Color(0, 255, 0, 26);
    private static final Color SOFT_COLOR = new Color(255, 255, 0, 26);
    private static final Color INVERT_COLOR = new Color(255, 0, 0, 26);

    public static void run(Graphics2D g, ScreenPoint[] selectionArea, Renderer renderer) {
        boolean isStrict = isStrict(selectionArea); // If the user dragged upwards, it's strict, otherwise it's soft.
//        boolean isInvert = selectionArea[0].x >= selectionArea[1].x; Commented out for now, as invert selection is implemented but this may be confusing.
        ScreenPoint i = selectionArea[0];
        ScreenPoint ii = selectionArea[1];
        g.setColor(isStrict ? STRICT_COLOR : SOFT_COLOR);
        Main.Cursors.set(isStrict ? "selectStrict" : "selectSoft");
//        if (isStrict) Main.Cursors.set("selectStrict");
//        else Main.Cursors.set("selectSoft");
        g.fillRect(Math.min(i.x, ii.x), Math.min(i.y, ii.y), Math.abs(i.x - ii.x), Math.abs(i.y - ii.y));
        SelectionQuery selectionQuery = new SelectionQuery(
                i, ii,
                isStrict ? SelectionType.BOUNDS_STRICT : SelectionType.BOUNDS_SOFT
        );
        SelectionManager m = renderer.select(selectionQuery);
        log.println("Selected " + m.getSelected().size() + " objects.");
    }

    public static boolean isStrict(ScreenPoint [] selectionArea) {
        return selectionArea[0].y >= selectionArea[1].y;
    }
}
