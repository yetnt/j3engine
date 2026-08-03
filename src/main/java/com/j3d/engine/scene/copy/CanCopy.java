package com.j3d.engine.scene.copy;

import com.j3d.engine.interact.cmd.commands.clipboard.*;

/**
 * Interface for objects that can be copied.
 * This allows for a standardised way to duplicate objects, potentially
 * with modifications specified by {@link CopyProperties}.
 * @author Lehlogonolo Poole
 * @see CopyProperties
 * @see Copy
 * @see InvalidCopyException
 * @see CopyCmd
 * @see PasteCmd
 */
public interface CanCopy {
    /**
     * Copies the properties of this object based on the provided {@link CopyProperties}.
     * The implementation should handle how the copying is performed,
     * potentially creating a new instance or modifying an existing one.
     * @param props The properties defining how the copy operation should be performed.
     * @throws InvalidCopyException If the copy operation cannot be completed due to invalid properties or state.
     */
    void copy(CopyProperties props) throws InvalidCopyException;
}
