package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.utility.Pair;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

enum Handle {
    X, Y, Z;

    @Override
    public String toString() {
        return this.name() + " Handle";
    }
}

public class ScaleMouseOwner extends MouseOwner {

    public ArrayList<ScreenPoint> handlePositions = new ArrayList<>();
    public Handle selectedHandle;
    public Pair<Integer, Integer> selectionBoundingBox = new Pair<>(80, 80);
    public Pair<Integer, Integer> startPos = new Pair<>(0, 0);
    public int distance = 0;


    /**
     * Checks if the MouseEvent lies within bounds of the ScreenPoint handle
     * @param e The mouse event
     * @param p The handle to check if its within bounds for
     * @return Whether this handle is in bounds
     */
    private boolean isWithinBounds(MouseEvent e, ScreenPoint p) {
        int x = e.getX();
        int y = e.getY();

        int lowerX = p.x - selectionBoundingBox.first;
        int lowerY = p.y - selectionBoundingBox.second;
        int upperX = p.x + selectionBoundingBox.first;
        int upperY = p.y + selectionBoundingBox.second;

        return x >= lowerX && x <= upperX && y >= lowerY && y <= upperY;
    }

    public ScaleMouseOwner() {
        super(MOwner.SCALE_HANDLE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isNotOwner()) return;
        handlePositions.forEach(p -> {
            if (isWithinBounds(e, p)) {
                System.out.println("A handle was clicked!" + " The " + (p.equals(handlePositions.getFirst()) ? "x" : p.equals(handlePositions.get(1)) ? "y" : "z"));
            }
        });

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        handlePositions.forEach(p -> {
            if (!isWithinBounds(e, p)) return;
            if (selectedHandle == null) {
                selectedHandle =
                        p.equals(handlePositions.getFirst()) ? Handle.X :
                                p.equals(handlePositions.get(1)) ? Handle.Y : Handle.Z;
                startPos = new Pair<>(e.getX(), e.getY());
                System.out.println("Bro started dragging " + selectedHandle);
            }

        });
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandle == null) return;
        int dx = e.getX() - startPos.first;
        int dy = e.getY() - startPos.second;

        int movement = switch (selectedHandle) {
            case X -> dx;
            case Y -> dy;
            case Z -> dx;
        };

        // TODO: Implement scale
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandle == null) return;
        System.out.println("Bro end drag " + selectedHandle);
        selectedHandle = null;
    }

    public void setHandlePositions(ArrayList<ScreenPoint> handlePositions) {
        this.handlePositions = handlePositions;
    }
}
