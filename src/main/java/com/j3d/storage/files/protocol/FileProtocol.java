package com.j3d.storage.files.protocol;

import com.j3d.storage.files.protocol.proj.ProjectFileV1;
import com.j3d.storage.files.protocol.proj.ProjectFileV2;
import com.j3d.ui.dialog.Spinner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * FileProtocol defines the methods required for reading and writing files
 * using a custom protocol.
 */
public interface FileProtocol {
    ProjectFileV1 projectFileV1 = new ProjectFileV1();
    ProjectFileV2 projectFileV2 = new ProjectFileV2();

    /**
     * Gets the protocol header string that uniquely identifies this specific file protocol.
     * @return The protocol header.
     */
    String getProtocolHeader() ;
    /**
     * Gets the version number of the protocol.
     * @return The protocol version.
     */
    int getProtocolVersion();

    /**
     * The protocol's extension
     * @return The protocol's extension
     */
    String getExtension();

    /**
     * Returns a set of {@link FileProtocol} instances to which this protocol can be converted (upgraded).
     * These are typically newer versions of the protocol.
     *
     * @return A set of higher version protocols this protocol can convert to.
     */
    Set<FileProtocol> convertibleToHigher();

    /**
     * Returns a set of {@link FileProtocol} instances from which this protocol can be converted (downgraded or upgraded from).
     * These are typically older versions of the protocol that can be read and converted into this protocol.
     *
     * @return A set of lower version protocols that can be converted to this protocol.
     */
    Set<FileProtocol> convertibleFromLower();

    /**
     * Reads a file from the specified path and returns its content as an object of type T. This should be
     * a direct inverse of the corresponding {@link #writeFile(String, String, ArrayList)} method.
     * @param path The path to the file to be read.
     * @param name The name of the file to be read.
     * @param throbber The Spinner instance
     * @return The content of the file as an object of type T.
     * @param <T> The type of the object to be returned.
     */
    <T extends ArrayList> T readFile(String path, String name, Spinner throbber) throws Exception;
    /**
     * Writes the provided data to a file at the specified path. This should be
     * a direct inverse of the corresponding {@link #readFile(String, String, Spinner)} method.
     * @param path The path to the file to be written.
     * @param data The data to be written to the file.
     * @param <T> The type of the data to be written, which must extend ArrayList.
     */
    <T extends ArrayList> void writeFile(String path, String name, T data);

    /**
     * Gets a Consumer that writes the protocol-specific header information
     * to a DataOutputStream.
     * @return A Consumer that writes the header.
     */
    Consumer<DataOutputStream> getHeaderWriter();
    /**
     * Gets a Consumer that reads the protocol-specific header information
     * from a DataInputStream.
     * @return A Consumer that reads the header.
     * @throws IOException If an I/O error occurs.
     */
    BiConsumer<DataInputStream, Integer> getHeaderReader() throws IOException;

}
