package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.interact.cmd.commands.transform.AbstractTransform;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.utility.Pair;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A specialized {@link MouseOwner} that manages user interaction with a set of
 * 3D transformation handles.
 * <p>
 * This class is the foundation for mouse-driven transformations. It takes ownership
 * of mouse input and is responsible for detecting when a user clicks on a {@link Handle},
 * effectively selecting an axis for manipulation. Subclasses, such as {@link TranslateMouseOwner}
 * or {@link ScaleMouseOwner}, extend this class to implement the specific logic for
 * dragging the handle and transforming the selected objects.
 *
 * @author Lehlogonolo Poole
 * @see MOwner
 * @see MouseOwner
 * @see Handle
 * @see AbstractTransform
 */
public class TransformMouseOwner extends MouseOwner {

    /** The list of 3D handles (e.g., for X, Y, Z axes) that this owner manages. */
    public ArrayList<Handle> handles = new ArrayList<>();
    /** The type of the currently selected handle, or null if no handle is selected. */
    public HandleType selectedHandleType;
    /** The screen-space bounding box (as a radius) for detecting clicks on a handle. */
    public Pair<Integer, Integer> selectionBoundingBox = new Pair<>(20, 20);
    /** A generic distance variable, often used by subclasses to track drag distance. */
    public int distance = 0;
    /** A direct reference to the currently selected handle object, or null. */
    public Handle selectedHandle;

    /**
     * Checks if a mouse event's coordinates are within the circular bounding box of a handle's screen point.
     * @param e The mouse event to check.
     * @param p The screen point of the handle.
     * @return {@code true} if the mouse click is on the handle, {@code false} otherwise.
     */
    private boolean isWithinBounds(MouseEvent e, ScreenPoint p) {
        int dx = e.getX() - p.x;
        int dy = e.getY() - p.y;
        int radius = selectionBoundingBox.first;

        // Pythagorean theorem: a^2 + b^2 <= r^2
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    /**
     * Constructs a new TransformMouseOwner.
     * @param mw The parent {@link MOwner} that manages all mouse owners.
     */
    public TransformMouseOwner(MOwner mw) {
        super(mw);
    }

    /**
     * An adapter method called when a handle is successfully pressed.
     * Subclasses can override this to perform specific actions upon handle selection.
     * @param handle The handle that was pressed.
     * @param e The originating mouse event.
     * @throws Exception if an error occurs during processing.
     */
    public void mousePressedAdapter(Handle handle, MouseEvent e) throws Exception {
        handle.selected();
    }

    /**
     * Handles the mouse pressed event to detect handle selection.
     * It iterates through all managed handles and checks if the click was within the bounds
     * of any of them. If a handle is clicked, it is marked as selected.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;

        // Reset selection state before checking for a new one
        if (this.selectedHandle != null) {
            this.selectedHandle.unselect();
        }
        this.selectedHandle = null;

        for (Handle p : handles) {
            if (isWithinBounds(e, p.toSp())) {
                this.selectedHandle = p;
                try {
                    mousePressedAdapter(p, e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return; // Exit immediately once a hit is found
            }
        }
    }

    /**
     * Handles the mouse released event. Subclasses often use this to finalize a transformation.
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        if (selectedHandleType == null) return;
        selectedHandleType = null;
    }

    /**
     * Sets the list of handles and the object references to be managed by this owner.
     * @param handles The list of {@link Handle} objects.
     * @param references The list of {@link GPoint}s being transformed (often unused in the base class).
     */
    public void setHandles(ArrayList<Handle> handles, ArrayList<GPoint> references) {
        this.handles = handles;
    }
}
