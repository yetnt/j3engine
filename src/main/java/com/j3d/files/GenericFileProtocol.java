package com.j3d.files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * GenericFileProtocol provides methods to read and write
 * a standard file header for J3D files.
 */
public class GenericFileProtocol {
    /**
     * The Header for all J3D files.
     */
    private static final String HEADER_TOP = "J3D";
    /**
     * The version of J3D itself that created the file.
     */
    private static final int VERSION = 1;

    /**
     * Writes the standard J3D file header to the provided DataOutputStream.
     * @param dos The DataOutputStream to write the header to.
     * @throws IOException If an I/O error occurs.
     */
    public void writeHeader(DataOutputStream dos) throws IOException {
        dos.writeUTF(HEADER_TOP);
        dos.writeInt(VERSION);
    }

    /**
     * Reads and validates the standard J3D file header from the provided DataInputStream.
     * @param dis The DataInputStream to read the header from.
     * @throws IOException If an I/O error occurs or if the header is invalid.
     */
    public void readHeader(DataInputStream dis) throws IOException {
        String header = dis.readUTF();
        int version = dis.readInt();
        if (!HEADER_TOP.equals(header)) {
            throw new IOException("Invalid J3D file header: " + header);
        }
        if (version != VERSION) {
            throw new IOException("Unsupported file version: " + version);
        }
    }
}
