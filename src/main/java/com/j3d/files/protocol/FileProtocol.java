package com.j3d.files.protocol;

import com.j3d.ui.util.Throbber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * FileProtocol defines the methods required for reading and writing files
 * using a custom protocol.
 */
public interface FileProtocol {
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
     * Reads a file from the specified path and returns its content as an object of type T. This should be
     * a direct inverse of the corresponding {@link #writeFile(String, String, ArrayList)} method.
     * @param path The path to the file to be read.
     * @param name The name of the file to be read.
     * @param throbber The Throbber instance
     * @return The content of the file as an object of type T.
     * @param <T> The type of the object to be returned.
     */
    <T extends ArrayList> T readFile(String path, String name, Throbber throbber) throws IOException;
    /**
     * Writes the provided data to a file at the specified path. This should be
     * a direct inverse of the corresponding {@link #readFile(String, String, Throbber)} method.
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
    Consumer<DataInputStream> getHeaderReader() throws IOException;
}
