package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.utility.Pair;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 */
public class TransformMouseOwner extends MouseOwner implements TransformContract {

    public ArrayList<Handle> handles = new ArrayList<>();
    public HandleType selectedHandleType;
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

    public TransformMouseOwner(MOwner mw) {
        super(mw);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isNotOwner()) return;
        handles.forEach(p -> {
            if (isWithinBounds(e, p.toSp())) {
                System.out.println("A handle was clicked!" + " The " + (p.equals(handles.getFirst()) ? "x" : p.equals(handles.get(1)) ? "y" : "z"));
            }
        });

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        handles.forEach(p -> {
            if (!isWithinBounds(e, p.toSp())) return;
            if (selectedHandleType == null) {
                selectedHandleType =
                        p.equals(handles.getFirst()) ? HandleType.X :
                                p.equals(handles.get(1)) ? HandleType.Y : HandleType.Z;
                startPos = new Pair<>(e.getX(), e.getY());
                System.out.println("Bro started dragging " + selectedHandleType);
            }

        });
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandleType == null) return;
        int dx = e.getX() - startPos.first;
        int dy = e.getY() - startPos.second;

        try {
            mouseDraggedAdapter(selectedHandleType, dx, dy, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandleType == null) return;
        System.out.println("Bro end drag " + selectedHandleType);
        selectedHandleType = null;
    }

    public void setHandles(ArrayList<Handle> handles) {
        this.handles = handles;
    }

    @Override
    public void mouseDraggedAdapter(HandleType selectedHandle, int dx, int dy, MouseEvent e) throws Exception {
        return;
    }

    /**
     * Calculates Point D (the new handle position) based on mouse movement.
     *
     * @param camPos    The 3D position of the camera (O)
     * @param rayDir    The normalized direction from camera through mouse point C (
     *                  This is the difference of the camera pos from point c normalized
     *                  {@code pointC.sub(camPos).normalize()})
     * @param handlePos The current 3D position of the handle (A)
     * @param axisDir   The normalized 3D direction of the axis (e.g., 1,0,0 for X)
     * @return The new 3D point D along the axis and it's distance from A
     *
     * @implNote
     *      <ul>{@code A} is the position of the axis in the world.</ul>
     *      <ul>{@code O} is the position of the camera</ul>
     *      <ul>{@code C} is where the mouse is dragged to in 2d space, projected onto the camera.</ul>
     *      <ul>{@code D} is the target point along the axis of the handle within 3D space.</ul>
     */
    protected Pair<Vector3, Double> calculateNewHandlePos(Vector3 camPos, Vector3 rayDir, Vector3 handlePos, Vector3 axisDir) {
        // Vector from the handle to the camera
        Vector3 w = new Vector3(camPos.getX() - handlePos.getX(), camPos.getY() - handlePos.getY(), camPos.getZ() - handlePos.getZ());

        double uu = rayDir.dot(rayDir);
        double uv = rayDir.dot(axisDir);
        double vv = axisDir.dot(axisDir);
        double wu = w.dot(rayDir);
        double wv = w.dot(axisDir);

        double denom = (uu * vv) - (uv * uv);

        // If looking head-on along the axis, the denominator is 0.
        // We return the original position to prevent "teleporting" handles.
        if (Math.abs(denom) < 1e-6) {
            return new Pair<>(handlePos, 0.0);
        }

        // s is the scalar distance along the axis from point A
        double s = (uv * wu - uu * wv) / denom;

        // Return Point D: A + (axis * s)
        return new Pair<>(new Vector3(
                handlePos.getX() - (axisDir.getX() * s),
                handlePos.getY() - (axisDir.getY() * s),
                handlePos.getZ() - (axisDir.getZ() * s)
        ), s);
    }
}
