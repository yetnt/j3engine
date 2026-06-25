package com.j3d.storage.files.protocol;

import com.j3d.errors.ErrorHandler;
import com.j3d.errors.J3DError;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.threads.LongTask;
import com.j3d.ui.engine.EngineFrame;

import java.io.File;
import java.util.function.BiConsumer;

/**
 * An exception who's pure purpose is Control flow.
 * <p>
 *     When using {@link ProjectFile} and {@link LongTask}, many anonymous lambdas can be created
 *     and we cannot easily go back to the first callee. This exception which {@link ProjectFile#handleErr(ProjectFile, Exception, BiConsumer)}
 *     handles, is how one {@link ProjectFile} can exit early during it's read consumer initialisation
 *     such as to alert that while the current file is a project file, it's of a different version
 *     which the current reader cannot read.
 * </p>
 * <p>
 *     As this is control flow and does not make it to the user (Unless {@link ProjectFile#handleErr(ProjectFile, Exception, BiConsumer)}
 *     specifies otherwise, it is not wrapped with {@link J3DError} or handled by {@link ErrorHandler}
 * </p>
 * @see ProjectFile#handleErr(ProjectFile, Exception, BiConsumer)
 * @see LongTask
 * @see ProjectFile
 * @see EngineFrame#readFileUsingVers(File, int)
 * @author Lehlogonolo Poole
 */
public class UnsupportedVersionException extends RuntimeException {
    public int version;
    public UnsupportedVersionException(String message, int version) {
        super(message);
        this.version = version;
    }
}
