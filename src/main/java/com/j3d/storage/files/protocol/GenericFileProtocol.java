package com.j3d.storage.files.protocol;

import com.j3d.errors.ErrorHandler;
import com.j3d.storage.errs.J3DFileException;

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
    public void writeHeader(DataOutputStream dos) {
        try {
            dos.writeUTF(HEADER_TOP);
            dos.writeInt(VERSION);
        } catch (IOException e) {
            ErrorHandler.handle(
                    new J3DFileException(
                            "Error writing file header. This file may be corrupted.", e
                    )
            );
        }
    }

    /**
     * Reads and validates the standard J3D file header from the provided DataInputStream.
     * @param dis The DataInputStream to read the header from.
     * @throws IOException If an I/O error occurs or if the header is invalid.
     */
    public void readHeader(DataInputStream dis) {
        try {
            String header = dis.readUTF();
            int version = dis.readInt();
            if (!HEADER_TOP.equals(header)) {
                ErrorHandler.handle(
                        new J3DFileException(
                                "Unsupported file header: " + header
                        )
                );
            }
            if (version != VERSION) {
                ErrorHandler.handle(
                        new J3DFileException(
                                "Unsupported file version: " + version
                        )
                );
            }
        } catch (IOException e) {
            ErrorHandler.handle(
                    new J3DFileException(
                            "Error reading file header. This file may be corrupted.", e
                    )
            );
        }
    }
}
