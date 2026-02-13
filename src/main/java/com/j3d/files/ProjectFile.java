package com.j3d.files;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.layer.LayerList;
import com.j3d.files.protocol.FileProtocol;
import com.j3d.files.protocol.GenericFileProtocol;
import com.j3d.ui.util.Throbber;
import com.j3d.utility.HashMultiMap;
import com.j3d.utility.Pair;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
 *         <ul>Colour Red (int)</ul>
 *         <ul>Colour Green (int)</ul>
 *         <ul>Colour Blue (int)</ul>
 *         <ul>Colour Alpha (int)</ul>
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
        return 1;
    }

    @Override
    public String getExtension() {
        return "j3p";
    }

    @Override
    public <T extends ArrayList> void writeFile(String path, String name, T data) {
        Consumer<DataOutputStream> fileWriter = dos -> {
            try {
                writeHeader(dos); // Write J3D file header
                getHeaderWriter().accept(dos); // Write PROJECT file header
                LayerList layers = (LayerList) data.clone();

                layers.removeFirst(); // Remove "BACKG" layer
                layers.removeIf(Layer::isForDeletion); // Remove layers that are deleted in memory

                dos.writeInt(layers.size()); // Write number of layers
                layers.forEach((layer) -> {
                    try {
                        dos.writeUTF(layer.getIdentifier()); // Write layer identifier
                        dos.writeBoolean(layer.isHidden()); // Write layer hidden state
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                ArrayList<Pair<Thing, GPoint>> points = new ArrayList<>();
                ArrayList<Pair<Thing, GLine>> lines = new ArrayList<>();
                ArrayList<Pair<Thing, GTri>> tris = new ArrayList<>();

                layers.stream()
                        .flatMap(Layer::stream).forEach(thing -> {
                            thing.getObjects().forEach(obj -> {
                                if (obj instanceof GPoint gp) {
                                    points.add(new Pair<>(thing, gp));
                                } else if (obj instanceof GLine gl) {
                                    lines.add(new Pair<>(thing, gl));
                                } else if (obj instanceof GTri gt) {
                                    tris.add(new Pair<>(thing, gt));
                                }
                            });
                        });

                // Write points
                dos.writeInt(points.size()); // Write number of points
                for (Pair<Thing, GPoint> pair : points) {
                    GPoint gp = pair.second;
                    Thing parent = pair.first;
                    dos.writeUTF(gp.getId().toString()); // Write Point UUID
                    dos.writeUTF(parent.getId().toString()); // Write Parent Thing UUID
                    dos.writeDouble(gp.getPivot().getX()); // Write X Coordinate
                    dos.writeDouble(gp.getPivot().getY()); // Write Y Coordinate
                    dos.writeDouble(gp.getPivot().getZ()); // Write Z Coordinate
                }
                // Write lines
                dos.writeInt(lines.size());
                for (Pair<Thing, GLine> pair : lines) {
                    GLine gl = pair.second;
                    Thing parent = pair.first;
                    dos.writeUTF(gl.getId().toString());
                    dos.writeUTF(parent.getId().toString());
                    dos.writeUTF(gl.getStart().getId().toString());
                    dos.writeUTF(gl.getEnd().getId().toString());
                }
                // Write triangles
                dos.writeInt(tris.size());
                for (Pair<Thing, GTri> pair : tris) {
                    GTri gt = pair.second;
                    Thing parent = pair.first;
                    dos.writeUTF(gt.getId().toString());
                    dos.writeUTF(parent.getId().toString());
                    // write colour
                    dos.writeInt(gt.getColour().getRed());
                    dos.writeInt(gt.getColour().getGreen());
                    dos.writeInt(gt.getColour().getBlue());
                    dos.writeInt(gt.getColour().getAlpha());
                    // write legs
                    dos.writeUTF(gt.getLegA().getId().toString());
                    dos.writeUTF(gt.getLegB().getId().toString());
                    dos.writeUTF(gt.getLegC().getId().toString());
                }

                layers = LayerList.from(layers.stream().filter(
                        thing -> !thing.isForDeletion()
                ).collect(Collectors.toList()));


                for  (int i = 0; i < layers.size(); i++) {
                    dos.writeInt(i); // layer index
                    var layer = layers.get(i);
                    dos.writeInt(layer.size());
                    layers.get(i).forEach((thing) -> {
                       // write thing data
                        try {
                            dos.writeUTF(thing.getId().toString());
                            dos.writeUTF(thing.getName());
                            dos.writeBoolean(thing.isHidden());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            msg("Wrote project file successfully to " + path);
        };

        FilesUtility.writeBinary(path, name, fileWriter);
    }

    private static void msg(String message) {
        Static.log.println(message);
    }

    /**
     * Reads a project file from the specified path and constructs the entire
     * project structure including layers and things.
     * @param path The path to the file to be read.
     * @return An ArrayLit containing the success state of the read operation.
     * @param <T> The type of ArrayList to be returned.
     */
    @Override
    public <T extends ArrayList> T readFile(String path, String name, Throbber throbber) throws Exception {
        final ArrayList<Interactable> interactables =  new ArrayList<>();
        ArrayList<Boolean> success = new ArrayList<>(1);

        final HashMap<String, Layer> layersMap = new HashMap<>();
        final List<Layer> layerOrder = new ArrayList<>();
        final HashMultiMap<String, GPoint> pointsParentsMap = new HashMultiMap<>();
        final HashMap<String, GPoint> pointsMap = new HashMap<>();
        final HashMultiMap<String, GLine> linesParentsMap = new HashMultiMap<>();
        final HashMap<String, GLine> linesMap = new HashMap<>();
        final HashMultiMap<String, GTri> trisParentsMap = new HashMultiMap<>();
        final HashMap<String, GTri> trisMap = new HashMap<>();

        Static.renderer.layers.removeIf(l -> !Objects.equals(l.getIdentifier(), Layer.backgroundId));

        IOSupplier<DataInputStream> fileReader = dis -> {
            try {
                msg("Reading project file from " + path);
                readHeader(dis); // Read J3D file header
                getHeaderReader().accept(dis); // Read PROJECT file header

                msg("Reading layers");
                int numLayers = dis.readInt(); // Read number of layers
                msg(numLayers + " layers");
                throbber.progressStart("Reading layers", numLayers);
                for (int i = 0; i < numLayers; i++) {
                    String layerId = dis.readUTF(); // Read layer identifier
                    boolean isHidden = dis.readBoolean(); // Read layer hidden state
                    Layer l = Layer.fromRaw(layerId, isHidden);
                    layersMap.put(layerId, l);
                    layerOrder.add(l);
                    throbber.updateProgress(i + 1);
                    interactables.add(l);
                    msg("Read layer " + layerId);
                }

                msg("Reading things");
                int numPoints = dis.readInt(); // Read number of points
                msg(numPoints + " points");
                if (numPoints != 0) {
                    throbber.progressStart("Reading points", numPoints);
                    for (int i = 0; i < numPoints; i++) {
                        String pointUUID = dis.readUTF(); // Read Point UUID
                        String parentThingUUID = dis.readUTF(); // Read Parent Thing UUID
                        double x = dis.readDouble(); // Read X Coordinate
                        double y = dis.readDouble(); // Read Y Coordinate
                        double z = dis.readDouble(); // Read Z Coordinate
                        GPoint point = GPoint.fromRaw(pointUUID, new Vector3(x, y, z));
                        pointsParentsMap.putValue(parentThingUUID, point);
                        pointsMap.put(pointUUID, point);
                        throbber.updateProgress(i + 1);
                        msg("Read point " + pointUUID);
                    }
                }

                msg("Reading lines");
                int numLines = dis.readInt(); // Read number of lines
                msg(numLines + " lines");
                if (numPoints == 0 && numLines != 0)
                    throw new IOException("Invalid project file: missing points");

                if (numLines != 0) {
                    throbber.progressStart("Reading lines", numLines);
                    for (int i = 0; i < numLines; i++) {
                        String lineUUID = dis.readUTF();
                        String parentThingUUID = dis.readUTF();
                        String startPointUUID = dis.readUTF();
                        String endPointUUID = dis.readUTF();
                        // Create and store line as needed
                        GPoint startPoint = pointsMap.get(startPointUUID);
                        GPoint endPoint = pointsMap.get(endPointUUID);
                        if (startPoint == null || endPoint == null)
                            throw new IOException("Invalid line definition: missing points");

                        GLine line = GLine.fromRaw(lineUUID, startPoint, endPoint);
                        linesParentsMap.putValue(parentThingUUID, line);
                        linesMap.put(lineUUID, line);
                        throbber.updateProgress(i + 1);
                        msg("Read line " + lineUUID);
                    }
                }

                msg("Reading triangles");
                int numTris = dis.readInt(); // Read number of triangles
                msg(numTris + " triangles");
                if (numTris != 0) {
                    throbber.progressStart("Reading triangles", numTris);
                    for (int i = 0; i < numTris; i++) {
                        String triUUID = dis.readUTF();
                        String parentThingUUID = dis.readUTF();
                        msg("Reading triangle " + triUUID);
                        int colorR = dis.readInt();
                        int colorG = dis.readInt();
                        int colorB = dis.readInt();
                        int colorA = dis.readInt();
                        msg("Color: " + colorR + ", " + colorG + ", " + colorB + ", " + colorA);
                        Color triColor = new Color(colorR, colorG, colorB, colorA);
                        String legAUUID = dis.readUTF();
                        String legBUUID = dis.readUTF();
                        String legCUUID = dis.readUTF();
                        msg("Legs: " + legAUUID + ", " + legBUUID + ", " + legCUUID);
                        GLine legA = linesMap.get(legAUUID);
                        GLine legB = linesMap.get(legBUUID);
                        GLine legC = linesMap.get(legCUUID);
                        if (legA == null || legB == null || legC == null) {
                            throw new IOException("Invalid triangle definition: missing legs");
                        }
                        trisParentsMap.putValue(parentThingUUID, GTri.fromRaw(triUUID, triColor, legA, legB, legC));
                        throbber.updateProgress(i + 1);
                        msg("Read triangle " + triUUID);
                    }
                }

                msg("Reading layers");
                for (int i = 0; i < numLayers; i++) {
                    int layerIndex = dis.readInt(); // layer index
                    msg("Reading layer " + layerIndex);
                    Layer l = layerOrder.get(layerIndex);
                    if (l == null) throw new IOException("Invalid layer index: " + layerIndex);
                    int numThingsInLayer = dis.readInt();
                    msg("\t"+numThingsInLayer + " things");
                    if (numThingsInLayer == 0) continue;

                    throbber.progressStart("Reading things in layer " + l.getIdentifier(), numThingsInLayer);
                    for (int j = 0; j < numThingsInLayer; j++) {
                        String thingUUID = dis.readUTF();
                        msg("\tReading thing " + thingUUID);
                        String thingName = dis.readUTF();
                        msg("\t\tName: " + thingName);
                        boolean thingHidden = dis.readBoolean();
                        msg("\t\tHidden: " + thingHidden);
                        Thing thing = Thing.fromRaw(thingName, thingUUID, thingHidden, l, Static.renderer)
                                .addObjs(
                                        pointsParentsMap.getValues(thingUUID).toArray(new GPoint[0])
                                )
                                .addObjs(
                                        linesParentsMap.getValues(thingUUID).toArray(new GLine[0])
                                )
                                .addObjs(
                                        trisParentsMap.getValues(thingUUID).toArray(new GTri[0])
                                );
                        interactables.add(thing);
                        throbber.updateProgress(j + 1);
                        msg("\tRead thing " + thingUUID);
                    }
                }

                throbber.setTaskTitle("Finalizing");

                Static.renderer.layers.addAll(layerOrder);

                Static.log.println("Project file loaded successfully from " + path);
                msg("Project file loaded successfully");
                // TODO: Fix object ghost state. probably something to do with TriStateArea
                // (TO reproduce, import a file and then change TriStateArea sort method.)
                // TODO: Fix selection not working on imported scene.
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        };
        FilesUtility.readBinary(path, name, fileReader);
//        success.add(true);
        return (T) interactables;
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
