package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.utility.Pair;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 */
public class TransformMouseOwner extends MouseOwner {

    public ArrayList<Handle> handles = new ArrayList<>();
    public HandleType selectedHandleType;
    public Pair<Integer, Integer> selectionBoundingBox = new Pair<>(80, 80);
    public int distance = 0;
    public Handle selectedHandle;


    /**
     * Checks if the MouseEvent lies within bounds of the ScreenPoint handle
     * @param e The mouse event
     * @param p The handle to check if its within bounds for
     * @return Whether this handle is in bounds
     */

    private boolean isWithinBounds(MouseEvent e, ScreenPoint p) {
        int dx = e.getX() - p.x;
        int dy = e.getY() - p.y;
        int radius = selectionBoundingBox.first;

        // Pythagorean: a^2 + b^2 <= r^2
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    public TransformMouseOwner(MOwner mw) {
        super(mw);
    }


    public void mousePressedAdapter(Handle handle, MouseEvent e) throws Exception {
        handle.selected();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;

        // Reset before checking
        this.selectedHandle = null;
        handles.stream().forEach(
                Handle::unselect
        );

        for (Handle p : handles) {
            if (isWithinBounds(e, p.toSp())) {
                this.selectedHandle = p;

                // Helpful Debug
                String name = (p == handles.get(0)) ? "X" : (p == handles.get(1)) ? "Y" : "Z";
                System.out.println("A handle was clicked! The " + name);
                try {
                    mousePressedAdapter(p, e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return; // EXIT IMMEDIATELY once we find a hit
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandleType == null) return;
//        System.out.println("Bro end drag " + selectedHandleType);
        selectedHandleType = null;
    }

    public void setHandles(ArrayList<Handle> handles, ArrayList<GPoint> references) {
        this.handles = handles;
    }

}
