package com.j3d.files;

import com.j3d.engine.layer.Layer;
import com.j3d.engine.layer.LayerList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * ProjectFile is a J3D file protocol implementation for reading and writing
 * project files that contain layer and thing information to and from disk.
 * <p>
 *     Typically .j3p files
 * </p>
 * <p>
 *     The Exact format is as follows:
 *     <ul>J3D Header (UTF-8)</ul>
 *     <ul>J3D Version (int)</ul>
 *     <ul>PROJECT Header (UTF-8)</ul>
 *     <ul>PROJECT Header Version (int)</ul>
 *     <ul>Number of Layers (int)</ul>
 *     <ul>For each Layer:
 *         <ul>Layer Identifier (UTF-8)</ul>
 *         <ul>Layer Hidden (boolean)</ul>
 *     </ul>
 *     <ul>Number of points in project (int)</ul>
 *     <ul>For each point:
 *         <ul>Point UUID (UTF-8)</ul>
 *         <ul>Thing Parent UUID (UTF-8)</ul>
 *         <ul>X Coordinate (double)</ul>
 *         <ul>Y Coordinate (double)</ul>
 *         <ul>Z Coordinate (double)</ul>
 *     </ul>
 *     <ul>Number of lines in project (int)</ul>
 *     <ul>For each line:
 *         <ul>Line UUID (UTF-8)</ul>
 *         <ul>Thing Parent UUID (UTF-8)</ul>
 *         <ul>Start Point UUID (UTF-8)</ul>
 *         <ul>End Point UUID (UTF-8)</ul>
 *     </ul>
 *     <ul>Number of triangles in project (int)</ul>
 *     <ul>For each triangle:
 *         <ul>Triangle UUID (UTF-8)</ul>
 *         <ul>Thing Parent UUID (UTF-8)</ul>
 *         <ul>Line 1 UUID (UTF-8)</ul>
 *         <ul>Line 2 UUID (UTF-8)</ul>
 *         <ul>Line 3 UUID (UTF-8)</ul>
 *     </ul>
 *     <ul>For each Layer:
 *         <ul>Layer Index (int)</ul>
 *         <ul>Number of Things in Layer (int)</ul>
 *         <ul>For each Thing:
 *             <ul>Thing UUID (UTF-8)</ul>
 *             <ul>Thing Name (UTF-8)</ul>
 *             <ul>Thing Visibility (boolean)</ul>
 *             <ul>... Multiple lines UUIDs of lines points and tris that the thing owns (UTF-8)</ul>
 *         </ul>
 *     </ul>
 *
 * </p>
 */
public class ProjectFile extends GenericFileProtocol implements FileProtocol {

    @Override
    public String getProtocolHeader() {
        return "PROJECT";
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public <T extends ArrayList> void writeFile(String path, T data) {
        Consumer<DataOutputStream> fileWriter = dos -> {
            try {
                writeHeader(dos);
                getHeaderWriter().accept(dos);
                LayerList layers = (LayerList) data.clone();

                layers.removeFirst(); // Remove "BACKG" layer
                layers.removeIf(Layer::isForDeletion);

                dos.writeInt(layers.size()); // number of layers
                layers.forEach((layer) -> {
                    try {
                        dos.writeUTF(layer.getIdentifier());
                        dos.writeBoolean(layer.isHidden());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                for  (int i = 0; i < layers.size(); i++) {
                    dos.writeInt(i); // layer index

                    // Write each Thing within the layer
                    //TODO: implement
                    /*
                        N = number of points
                        J = Number of lines
                        G = number of triangles


                        N
                        point-UUID
                        thingParent-UUUD
                        X
                        Y
                        Z
                        (all points in the scene...)
                        J
                        line-UUID
                        thingParent-UUID
                        startPoint-UUID
                        endPoint-UUID
                        (all lines in the scene...)
                        G
                        tri-UUID
                        thingParrnt-UUID
                        line1-UUID
                        line2-UUID
                        line3-UUID
                        (all tris in the scene...)
                        layer-index
                        thing-UUID
                        thing-name
                        thing-visibility
                        (things....)
                     */
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public <T> T readFile(String path) {
        return null; //TODO: implement
    }

    @Override
    public Consumer<DataOutputStream> getHeaderWriter() {
        return dataOutputStream -> {
            try {
                dataOutputStream.writeUTF(getProtocolHeader());
                dataOutputStream.writeInt(getProtocolVersion());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Consumer<DataInputStream> getHeaderReader() throws IOException {
        return dataInputStream -> {
            try {
                String head = dataInputStream.readUTF();
                int version = dataInputStream.readInt();

                if (!head.equals(getProtocolHeader())) {
                    throw new IOException("Invalid Project file header: " + head);
                }
                if (version != getProtocolVersion()) {
                    throw new IOException("Unsupported Project file version: " + version);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
