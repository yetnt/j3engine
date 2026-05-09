package com.j3d.engine.interact.cmd.commands.transform.handles;

import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;

/**
 * Represents the type or axis of a transformation handle.
 * <p>
 * This enum is used to identify which axis (X, Y, or Z) a specific {@link Handle}
 * corresponds to. This is fundamental for determining the direction of a mouse-driven
 * transformation in classes like {@link TransformMouseOwner}.
 *
 * @see Handle
 * @see TransformMouseOwner
 * @author Lehlogonolo Poole
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
