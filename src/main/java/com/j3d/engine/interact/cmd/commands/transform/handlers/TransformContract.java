package com.j3d.engine.interact.cmd.commands.transform.handlers;

import java.awt.event.MouseEvent;

/**
 * Contract for transform handlers that respond to mouse-drag interactions
 * on transform handles (e.g. X/Y/Z axis handles used to translate, rotate,
 * or scale a selection).
 *
 * <p>Implementations of this interface are expected to perform the actual
 * transformation logic when the user drags a handle on screen. Each concrete
 * handler corresponds to one transform operation (for example, scaling,
 * translating, or rotating).</p>
 *
 * <p>Typical usage:
 * <ol>
 *   <li>A {@link TransformMouseOwner} detects which handle is being dragged and
 *       computes an integer delta for the drag (dx, dy).</li>
 *   <li>It calls the implementing class' {@code mouseDraggedAdapter(...)} method to
 *       apply the appropriate transform to the selected objects.</li>
 * </ol>
 * </p>
 */
public interface TransformContract {
    /**
     * Called when the mouse is dragged while a transform handle is selected.
     *
     * @param selectedHandle the handle being dragged (represents which axis or
     *                       handle type the drag applies to). Implementations
     *                       should use this to decide how to interpret dx/dy.
     * @param dx the horizontal displacement (in screen pixels) from the drag start.
     * @param dy the vertical displacement (in screen pixels) from the drag start.
     * @param e The mouse event from {@link java.awt.event.MouseAdapter#mouseDragged(MouseEvent)}
     * @throws Exception any exception that can occur during transformation;
     *                   callers should handle or propagate errors as appropriate.
     */
    public void mouseDraggedAdapter(HandleType selectedHandle, int dx, int dy, MouseEvent e) throws Exception;
}
