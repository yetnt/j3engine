package com.j3d.errors;

import com.j3d.engine.EngineException;
import com.j3d.engine.interact.InteractionException;
import com.j3d.engine.math.MathException;
import com.j3d.engine.scene.SceneException;
import com.j3d.errors.severity.J3ErrSeverity;
import com.j3d.gen.GenException;
import com.j3d.storage.errs.DBException;
import com.j3d.storage.errs.GenericIOException;
import com.j3d.storage.errs.ProjectFileException;
import com.j3d.threads.ThreadsException;

/**
 * Defines base error codes for different modules or categories within J3Engine
 * These base codes are intended to be used within the specific {@link J3DError} child classes.
 * @see J3DError
 * @see J3ErrSeverity
 * @see ErrorHandler
 * @author Lehlogonolo Poole
 */
public enum BaseErrorCodes {

    /**
     * Base code for {@link GenException}
     */
    GENERATION(10),
    /**
     * Base code for {@link DBException}
     */
    DATABASE(20),
    /**
     * Base code for {@link ThreadsException}
     */
    THREADING(30),

    /**
     * Base code for {@link EngineException}
     */
    ENGINE_CORE(40),
    /**
     * Base code for {@link SceneException}
     */
    ENGINE_CORE_SCENE(41),
    /**
     * Base code for {@link MathException}
     */
    ENGINE_CORE_MATH(42),
    /**
     * Base code for {@link InteractionException}
     */
    ENGINE_CORE_INTERACT(43),

    /**
     * Base code for {@link GenericIOException}
     */
    IO(50),
    /**
     * Base code for {@link ProjectFileException}
     */
    IO_PROJECT(51);

    /**
     * Constructs a BaseErrorCodes enum constant with the specified integer base code.
     * @param i The integer base code for the error category.
     */
    BaseErrorCodes(int i) {
        baseCode = i;
    }
    private final int baseCode;

    /**
     * Returns the integer base error code associated with this enum constant.
     * @return The integer base error code.
     */
    public int getBaseCode() {
        return baseCode;
    }
}
