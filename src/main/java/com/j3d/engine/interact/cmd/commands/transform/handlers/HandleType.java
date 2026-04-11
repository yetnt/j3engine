package com.j3d.engine.interact.cmd.commands.transform.handlers;

/**
 * Represents the type or axis of a transformation handle.
 * <p>
 * This enum is used to identify which axis (X, Y, or Z) a specific {@link Handle}
 * corresponds to. This is fundamental for determining the direction of a mouse-driven
 * transformation in classes like {@link com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner}.
 *
 * @see Handle
 * @see com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner
 */
public enum HandleType {
    /** Represents the X-axis. */
    X,
    /** Represents the Y-axis. */
    Y,
    /** Represents the Z-axis. */
    Z;

    @Override
    public String toString() {
        return this.name() + " HandleType";
    }
}
