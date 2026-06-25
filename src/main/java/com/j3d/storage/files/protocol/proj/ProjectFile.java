package com.j3d.storage.files.protocol.proj;

import com.j3d.Static;
import com.j3d.errors.ErrorHandler;
import com.j3d.storage.errs.J3DFileException;
import com.j3d.storage.errs.ProjectFileException;
import com.j3d.storage.files.protocol.FileProtocol;
import com.j3d.storage.files.protocol.GenericFileProtocol;
import com.j3d.storage.files.protocol.UnsupportedVersionException;
import com.j3d.threads.LongTask;
import com.j3d.ui.dialog.Spinner;
import com.j3d.ui.engine.EngineFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *  ProjectFile is an abstract base class for J3D project file protocols.
 * It defines the common interface and properties for reading and writing
 * project files (typically .j3p files) of different versions.
 * <p>
 * Concrete implementations, such as {@link ProjectFileV1} and {@link ProjectFileV2},
 * handle the specific serialization and deserialization logic for their respective
 * file formats.
 * </p>
 * <p>
 * This class also provides utility methods for retrieving specific protocol
 * versions based on an integer identifier.
 * </p>
 * @author Lehlogonolo Poole
 */
public class ProjectFile extends GenericFileProtocol implements FileProtocol {

    private final int VERSION;
    private final String HEADER = "PROJECT";
    private final String EXTENSION = "j3p";
    private final Set<PF> convertibleTo;
    private final Set<PF> convertibleFrom;

    /**
     * Da default constructor
     * @param version The version of this project file protocol
     * @param convertibleTo The set of FileProtocol that this can be upgraded to.
     * @param convertibleFrom The set of FileProtocol that can upgrade to this.
     */
    public ProjectFile(int version, Set<PF> convertibleTo, Set<PF> convertibleFrom) {
        this.VERSION = version;
        this.convertibleTo = convertibleTo;
        this.convertibleFrom = convertibleFrom;
    }


    @Override
    public String getProtocolHeader() {
        return HEADER;
    }

    @Override
    public int getProtocolVersion() {
        return VERSION;
    }

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    public Set<PF> convertibleToHigher() {
        return convertibleTo;
    }

    @Override
    public Set<PF> convertibleFromLower() {
        return convertibleFrom;
    }

    @Override
    public <T extends ArrayList> T readFile(String path, String name, Spinner throbber) throws Exception {
        return null;
    }

    @Override
    public <T extends ArrayList> void writeFile(String path, String name, T data) {

    }

    @Override
    public Consumer<DataOutputStream> getHeaderWriter() {
        return dataOutputStream -> {
            try {
                dataOutputStream.writeUTF(getProtocolHeader());
                dataOutputStream.writeInt(getProtocolVersion());
            } catch (IOException e) {
                ErrorHandler.handle(
                        new ProjectFileException("Error writing project file header", e)
                );
            }
        };
    }

    @Override
    public BiConsumer<DataInputStream, Integer> getHeaderReader() throws IOException {
        return (dataInputStream, i)-> {
            try {
                String head = dataInputStream.readUTF();
                int version = dataInputStream.readInt();

                if (!head.equals(getProtocolHeader())) {
                    ErrorHandler.handle(
                            new ProjectFileException("Unsupported Project file header: " + head)
                    );
                }
                if (version > getProtocolVersion()) {
                    // No version can read or possibly convert to a higher version.
                    ErrorHandler.handle(
                            new ProjectFileException("Unsupported Project file version: " + version)
                    );
                } else {
                    // old version.
                    throw new UnsupportedVersionException("An unsupported version ("+version+") was used to load a project file of version (2)", version);
                }
            } catch (IOException e) {
                ErrorHandler.handle(
                        new ProjectFileException("Error reading project file header", e)
                );
            }
        };
    }

    public static ProjectFile getFromVersion(int version) {
        return switch (version) {
            case 1 -> Static.projectFileV1;
            case 2 -> Static.projectFileV2;
            default -> {
                ErrorHandler.handle(
                        new J3DFileException("Attempt to find a J3D project version which does not exist")
                );
                yield null;
            }
        };
    }

    /**
     * Handles errors that occur during file loading, particularly {@link UnsupportedVersionException}
     * which indicates an attempt to load an older version of a project file.
     * @param vers The current {@link ProjectFile} instance being used for loading.
     * @param err The exception that occurred during loading.
     * @param loadable A {@link BiConsumer} that accepts the current protocol version and the convertible protocol
     *                 if an {@link UnsupportedVersionException} is caught and a conversion is possible.
     * @implSpec This is intended to catch errors within a {@link LongTask}, an example can be find
     * within {@link EngineFrame#readFileUsingVers(File, int)}. Where a {@link UnsupportedVersionException}
     * is wrapped within {@link RuntimeException#getCause()}.
     * This is because, the specific {@link ProjectFile} itself will throw {@link UnsupportedVersionException}
     * and due to the operation being within a lambda, it needs a try-catch block, which wraps that error
     * within a {@link RuntimeException}.
     */
    public static void handleErr(ProjectFile vers, Exception err, BiConsumer<Integer, ProjectFile> loadable) {

        if (!(err instanceof RuntimeException rt)) {
            ErrorHandler.handle(
                    new ProjectFileException(err.getMessage(), err)
            );
            return;
        }

        if (err.getCause() instanceof UnsupportedVersionException uve) {
            int vOld = uve.version;
            FileProtocol convTo = (
                    uve.version < vers.getProtocolVersion()
                            ? vers.convertibleFromLower()
                            : vers.convertibleToHigher()
                    )
                    .stream()
                    .map(
                            ProjectFile.PF::getProjectFile
                    )
                    .filter(
                            v -> v.getProtocolVersion() == vOld
                    ).findFirst().orElse(null);
            if (!(convTo instanceof ProjectFile pj)) {
                ErrorHandler.handle(
                        new ProjectFileException(
                                "Attempt to load a project file version which cannot be loaded" +
                                        " on this version of J3Engine."
                        )
                );
                return;
            }
            loadable.accept(vers.getProtocolVersion(), pj);
        }
    }

    public enum PF {
        V1(Static.projectFileV1),
        V2(Static.projectFileV2);

        PF(ProjectFile pf) {
            projectFile = pf;
        }

        private ProjectFile projectFile;

        public ProjectFile getProjectFile() {
            return  projectFile;
        }
    }
}
