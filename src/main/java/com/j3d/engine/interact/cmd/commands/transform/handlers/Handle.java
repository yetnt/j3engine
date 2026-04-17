package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a single, interactive 3D handle (gizmo) used for manipulating objects in the scene.
 * <p>
 * A handle is a visual component (e.g., an arrow or a cube) that the user can click and
 * drag to perform a transformation (like translate, rotate, or scale) along a specific
 * axis. This class encapsulates the handle's 3D position, its type (axis), its selection
 * state, and the logic required to draw it on the screen.
 *
 * @author Lehlogonolo Poole
 * @see HandleType
 * @see TransformMouseOwner
 */
public class Handle {
    private final HandleType handleType;
    private Vector3 position;
    private boolean selected = false;
    private final BiConsumer<Graphics2D, ScreenPoint> draw;
    private Consumer<Graphics2D> extraDetail;

    /**
     * Constructs a new Handle.
     *
     * @param handleType The type or axis of this handle (X, Y, or Z).
     * @param position   The initial 3D position of the handle in world space.
     * @param draw       A {@link BiConsumer} containing the logic to draw the handle's shape
     *                   on the screen, given a {@link Graphics2D} context and a 2D {@link ScreenPoint}.
     */
    public Handle(HandleType handleType, Vector3 position, BiConsumer<Graphics2D, ScreenPoint> draw) {
        this.handleType = handleType;
        this.position = position;
        this.draw = draw;
    }

    /**
     * Sets an optional drawing function for rendering extra details when the handle is selected.
     *
     * @param extraDetail A {@link Consumer} to draw additional visual feedback.
     * @return This Handle instance for method chaining.
     */
    public Handle extraDetail(Consumer<Graphics2D> extraDetail) {
        this.extraDetail = extraDetail;
        return this;
    }

    /**
     * Renders the handle on the screen.
     * <p>
     * This method projects the handle's 3D position to a 2D screen point and executes
     * the drawing logic. If the handle is selected, it also renders its name and any
     * extra details.
     *
     * @param g The {@link Graphics2D} context to draw on.
     * @return This Handle instance.
     */
    public Handle draw(Graphics2D g) {
        draw.accept(g, toSp());
        if (selected) {
            if (extraDetail != null) {
                extraDetail.accept(g);
            }
            Static.sceneManager.drawText3D(
                    g, position.sub(new Vector3(0, 5, 0)),
                    handleType.name(), Static.camera,
                    new Color(0x000000),
                    Color.WHITE
            );
        }
        return this;
    }

    /**
     * Converts the handle's 3D world position to a 2D screen position.
     *
     * @return The calculated {@link ScreenPoint}.
     */
    public ScreenPoint toSp() {
        return position.toPoint(Static.camera).toScreen(Static.sceneManager);
    }

    /**
     * Returns the type (axis) of this handle.
     *
     * @return The {@link HandleType}.
     */
    public HandleType handleType() {
        return handleType;
    }

    /**
     * Updates the 3D position of the handle.
     *
     * @param newPos The new position {@link Vector3} in world space.
     */
    public void setPos(Vector3 newPos) {
        position = newPos;
    }

    /**
     * Marks the handle as not selected.
     */
    public void unselect() {
        selected = false;
    }

    /**
     * Marks the handle as selected.
     */
    public void selected() {
        selected = true;
    }

    /**
     * Gets the current 3D position of the handle.
     *
     * @return The position {@link Vector3}.
     */
    public Vector3 getPos() {
        return position;
    }
}
