package com.j3d.storage.files.protocol.proj;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.SceneObjectList;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.*;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.scene.nodes.layer.LayerList;
import com.j3d.storage.errs.ProjectFileException;
import com.j3d.storage.files.FilesUtility;
import com.j3d.storage.files.IOSupplier;
import com.j3d.storage.files.protocol.UnsupportedVersionException;
import com.j3d.ui.dialog.Spinner;
import com.j3d.utility.generic.HashMultiMap;
import com.j3d.utility.generic.Pair;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;


public class PF3 extends ProjectFile {

    public PF3() {
        super(3,
                Set.of(),     // Can convert to
                Set.of(PF.V2)  // Can convert from
        );
    }

    @Override
    public <T extends ArrayList> void writeFile(String path, String name, T data) {
        Consumer<DataOutputStream> fileWriter = dos -> {
            try {
                writeHeader(dos); // Write J3D file header
                getHeaderWriter().accept(dos); // Write PROJECT file header

                LayerList layers = (LayerList) StaticRefs.getSceneManager().layers.clone();

                layers.removeIf(l -> l.getName().equals(Layer.BACKGROUND_ID)); // Remove "BACKG" layer
                layers.removeIf(Layer::isForDeletion); // Remove layers that are deleted in memory

                // LAYERS
                writeLayers(dos, layers);

                // GET EVERYTHING

                ArrayList<Pair<UUID, GPoint>> points = new ArrayList<>();
                ArrayList<Pair<UUID, GLine>> lines = new ArrayList<>();
                ArrayList<Pair<UUID, GTri>> tris = new ArrayList<>();
                ArrayList<Pair<UUID, GCurve>> curves = new ArrayList<>();
                ArrayList<Pair<String, Thing>> things = new ArrayList<>();

                layers.forEach(layer -> layer.forEach(thing -> {
                            things.add(new Pair<>(layer.getName(), thing));
                            thing.getObjects().forEach(obj -> {
                                if (obj instanceof GPoint gp) {
                                    points.add(new Pair<>(thing.getId(), gp));
                                } else if (obj instanceof GLine gl) {
                                    lines.add(new Pair<>(thing.getId(), gl));
                                } else if (obj instanceof GTri gt) {
                                    tris.add(new Pair<>(thing.getId(), gt));
                                } else if (obj instanceof GCurve c) {
                                    curves.add(new Pair<>(thing.getId(), c));
                                }
                            });
                        })
                );

                // POINTS
                writePoints(dos, points);

                // LINES
                writeLines(dos, lines);

                // TRIS
                writeTris(dos, tris);

                // CURVES
                writeCurves(dos, curves);

                // POLYLINES WILL GO HERE

                // THINGS
                writeThings(dos, things);

                writeSixNine(dos);
            } catch (IOException e) {
                StaticRefs.getErrs().handle(
                        new ProjectFileException("Error writing project file data.", e)
                );
            }

            msg("Wrote project file successfully to " + path);
        };

        FilesUtility.writeBinary(path, name, fileWriter);
        StaticConfig.hasSaved = true;
        StaticRefs.getEngineFiles().recents.writeProj(new File(path).toPath().resolve(name).toFile());
    }

    @Override
    public <T extends ArrayList> T readFile(String path, String name, Spinner throbber) throws Exception {
        final ArrayList<SceneObjectList> sceneObjectLists =  new ArrayList<>();

        StaticRefs.getSceneManager().layers.removeIf(l -> !Objects.equals(l.getName(), Layer.BACKGROUND_ID));


        IOSupplier<DataInputStream> fileReader = dis -> {
            try {
                readHeader(dis);
                getHeaderReader().accept(dis, 3);

                HashMap<String, Layer> layers = readLayers(dis, throbber);
//                layers.put("Three Tris", new Layer("Three Tris")); // fix file
//                layers.put("Cube", new Layer("Cube"));
                sceneObjectLists.addAll(layers.values());

                final SuperMap<GPoint> points = readPoints(dis, throbber);
                final SuperMap<GLine> lines = readLines(dis, points, throbber);
                final SuperMap<GTri> tris = readTris(dis, lines, points, throbber);
                final SuperMap<GCurve> curves = readCurves(dis, points, throbber);

                HashMap<String, Thing> things = readThings(dis, layers, throbber);
                sceneObjectLists.addAll(things.values());

                things.forEach(
                        (s, t) -> t
                                .addObjs(points.fromThing(s).toArray(GPoint[]::new))
                                .addObjs(lines.fromThing(s).toArray(GLine[]::new))
                                .addObjs(tris.fromThing(s).toArray(GTri[]::new))
                                .addObjs(curves.fromThing(s).toArray(GCurve[]::new))
                );

                throbber.setTaskTitle("Finalizing");

                StaticRefs.getSceneManager().layers.addAll(layers.values());

                readSixNine(dis);

                msg("Project file of V3 loaded successfully from " + path);
            } catch (UnsupportedVersionException f) {
                throw f;
            } catch (IOException e) {
                StaticRefs.getErrs().handle(
                        new ProjectFileException("Error reading project file data.", e)
                );
            }
            return null;
        };

        FilesUtility.readBinary(path, name, fileReader);
        StaticConfig.hasSaved = true;
        StaticRefs.getEngineFiles().recents.writeProj(new File(path).toPath().resolve(name).toFile());

        return (T) sceneObjectLists;
    }

    /**
     * Writes a list of {@link Layer} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param layers The {@link LayerList} containing the layers to write.
     * @throws IOException If an I/O error occurs.
     */
    private void writeLayers(DataOutputStream dos, LayerList layers) throws IOException {
        beginList(dos);
        int layerAmt = layers.size();
        dos.writeInt(layerAmt);
        if (!layers.isEmpty()) {
            for (Layer l : layers) {
                beforeElement(dos);
                dos.writeUTF(l.getName());
                dos.writeBoolean(l.isHidden());
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link Layer} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link HashMap} where keys are layer names and values are {@link Layer} objects.
     * @throws IOException If an I/O error occurs or the file is corrupted.
     */
    private static HashMap<String, Layer> readLayers(DataInputStream dis, Spinner sp) throws IOException {
        HashMap<String, Layer> layers = new HashMap<>();
        consumeBeginList(dis);
        int size = dis.readInt();
        sp.progressStart("Reading layers", size);
        System.out.println("Discovered " + size + " layers");
        for (int i = 0; i < size; i++) {
            consumeBeforeElement(dis);
            String name = dis.readUTF();
            boolean hidden = dis.readBoolean();
            sp.updateProgress(i + 1);

            layers.put(name, Layer.fromRaw(name, hidden));
        }
        consumeEndList(dis);
        return layers;
    }

    /**
     * Writes a list of {@link Thing} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param things An {@link ArrayList} of {@link Pair}s, where each pair contains the parent {@link UUID} (as String) and the {@link Thing} to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeThings(DataOutputStream dos, ArrayList<Pair<String, Thing>> things) throws IOException {
        beginList(dos);
        int thingAmt = things.size();
        dos.writeInt(thingAmt);
        if (!things.isEmpty()) {
            for (Pair<String, Thing> pair : things) {
                Thing thing = pair.second;
                beforeElement(dos);
                dos.writeUTF(thing.getId().toString());
                dos.writeUTF(thing.getName());
                dos.writeUTF(pair.first);
                dos.writeBoolean(thing.isHidden());
                dos.writeBoolean(thing.isSolid());
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link Thing} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param layers A {@link HashMap} of existing {@link Layer}s, used to assign parent layers to things.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link HashMap} where keys are thing IDs (as String) and values are {@link Thing} objects.
     * @throws IOException If an I/O error occurs, the file is corrupted, or a parent layer is not found.
     */
    private static HashMap<String, Thing> readThings(DataInputStream dis, HashMap<String, Layer> layers, Spinner sp) throws IOException {
        consumeBeginList(dis);
        HashMap<String, Thing> out = new HashMap<>();
        int thingAmt = dis.readInt();
        sp.progressStart("Reading things", thingAmt);
        for (int i = 0; i < thingAmt; i++) {
            consumeBeforeElement(dis);
            String id = dis.readUTF();
            String name = dis.readUTF();
            String parentLayerName = dis.readUTF();
            boolean isHidden = dis.readBoolean();
            boolean isSolid = dis.readBoolean();

            Layer l = layers.get(parentLayerName);
            if (l == null) {
                throw new IOException("Corrupted file: Parent layer '" + parentLayerName + "' not found for thing with ID " + id + ".");
            }

            Thing t =
                    Thing.fromRaw(
                            name, id, isHidden,
                            l
                    );
            if (isSolid)
                t.solidify();
            sp.updateProgress(i + 1);
            out.put(id, t);
        }
        consumeEndList(dis);
        return out;
    }

    /**
     * Writes a list of {@link GCurve} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param curves An {@link ArrayList} of {@link Pair}s, where each pair contains the parent {@link UUID} and the {@link GCurve} to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeCurves(DataOutputStream dos, ArrayList<Pair<UUID, GCurve>> curves) throws IOException {
        beginList(dos);
        int curveAmt = curves.size();
        dos.writeInt(curveAmt);
        if (!curves.isEmpty()) {
            for (Pair<UUID, GCurve> pair : curves) {
                beforeElement(dos);
                GCurve gc = pair.second;
                dos.writeUTF(gc.getId().toString());
                UUID parent = pair.first;
                dos.writeUTF(parent.toString());
                dos.writeInt(colToInt(gc.getColour()));
                dos.writeInt(gc.getAmount());
                dos.writeUTF(gc.getStart().getId().toString());
                dos.writeUTF(gc.getControlPoint().getId().toString());
                dos.writeUTF(gc.getEnd().getId().toString());
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link GCurve} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param pointSuperMap A {@link SuperMap} containing {@link GPoint} objects, used to resolve curve points.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link SuperMap} containing the read {@link GCurve} objects, mapped by their ID and parent thing ID.
     * @throws IOException If an I/O error occurs or the file is corrupted.
     */
    private static SuperMap<GCurve> readCurves(DataInputStream dis, SuperMap<GPoint> pointSuperMap, Spinner sp) throws IOException {
        consumeBeginList(dis);
        HashMap<String, GCurve> idMap = new HashMap<>();
        HashMultiMap<String, GCurve> thingParentMap = new HashMultiMap<>();
        int curveAmt = dis.readInt();
        sp.progressStart("Reading curves", curveAmt);
        for (int i = 0; i < curveAmt; i++) {
            consumeBeforeElement(dis);
            String id = dis.readUTF();
            String parent = dis.readUTF();
            int colour = dis.readInt();
            Color col = intToCol(colour);
            int amount = dis.readInt();
            String start = dis.readUTF();
            String control = dis.readUTF();
            String end = dis.readUTF();

            GCurve curve = GCurve.fromRaw(
                    id,
                    pointSuperMap.getIdMap().get(start),
                    pointSuperMap.getIdMap().get(control),
                    pointSuperMap.getIdMap().get(end),
                    col, amount
            );
            sp.updateProgress(i + 1);
            idMap.put(id, curve);
            thingParentMap.putValue(parent, curve);
        }
        consumeEndList(dis);
        return new SuperMap<>(
                thingParentMap, idMap
        );
    }

    /**
     * Writes a list of {@link GTri} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param tris An {@link ArrayList} of {@link Pair}s, where each pair contains the parent {@link UUID} and the {@link GTri} to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeTris(DataOutputStream dos, ArrayList<Pair<UUID, GTri>> tris) throws IOException {
        beginList(dos);
        int triAmt = tris.size();
        dos.writeInt(triAmt);
        if (!tris.isEmpty()) {
            for (Pair<UUID, GTri> pair : tris) {
                beforeElement(dos);
                GTri gt = pair.second;
                dos.writeUTF(gt.getId().toString());
                UUID parent = pair.first;
                dos.writeUTF(parent.toString());
                dos.writeInt(colToInt(gt.getColour()));

                dos.writeUTF(gt.getLegA().getId().toString());
                dos.writeUTF(gt.getLegB().getId().toString());
                dos.writeUTF(gt.getLegC().getId().toString());

                dos.writeUTF(gt.getWinding().first().getId().toString());
                dos.writeUTF(gt.getWinding().second().getId().toString());
                dos.writeUTF(gt.getWinding().third().getId().toString());

                dos.writeBoolean(gt.isDoubleSided());
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link GTri} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param lines A {@link SuperMap} containing {@link GLine} objects, used to resolve triangle legs.
     * @param points A {@link SuperMap} containing {@link GPoint} objects, used to resolve triangle winding points.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link SuperMap} containing the read {@link GTri} objects, mapped by their ID and parent thing ID.
     * @throws IOException If an I/O error occurs or the file is corrupted.
     */
    private static SuperMap<GTri> readTris(DataInputStream dis, SuperMap<GLine> lines, SuperMap<GPoint> points, Spinner sp) throws IOException {
        consumeBeginList(dis);
        HashMap<String, GTri> idMap = new HashMap<>();
        HashMultiMap<String, GTri> thingParentMap = new HashMultiMap<>();
        int triAmt = dis.readInt();
        sp.progressStart("Reading tris", triAmt);
        for (int i = 0; i < triAmt; i++) {
            consumeBeforeElement(dis);
            String id = dis.readUTF();
            String parent = dis.readUTF();
            int colour = dis.readInt();
            Color col = intToCol(colour);
            String legA = dis.readUTF();
            String legB = dis.readUTF();
            String legC = dis.readUTF();
            String w1 = dis.readUTF();
            String w2 = dis.readUTF();
            String w3 = dis.readUTF();
            boolean isDoubleSided = dis.readBoolean();

            GTri tri = GTri.fromV3Raw(
                    id, col,
                    lines.getIdMap().get(legA),
                    lines.getIdMap().get(legB),
                    lines.getIdMap().get(legC),
                    points.getIdMap().get(w1),
                    points.getIdMap().get(w2),
                    points.getIdMap().get(w3)
            );
            tri.setDoubleSided(isDoubleSided);
            sp.updateProgress(i + 1);
            idMap.put(id, tri);
            thingParentMap.putValue(parent, tri);
        }
        consumeEndList(dis);
        return new SuperMap<>(thingParentMap, idMap);
    }

    /**
     * Writes a list of {@link GLine} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param lines An {@link ArrayList} of {@link Pair}s, where each pair contains the parent {@link UUID} and the {@link GLine} to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeLines(DataOutputStream dos, ArrayList<Pair<UUID, GLine>> lines) throws IOException {
        beginList(dos);
        int lineAmt = lines.size();
        dos.writeInt(lineAmt);
        if (!lines.isEmpty()) {
            for (Pair<UUID, GLine> pair : lines) {
                beforeElement(dos);
                GLine gl = pair.second;
                dos.writeUTF(gl.getId().toString());
                UUID parent = pair.first;
                dos.writeUTF(parent.toString());
                dos.writeInt(colToInt(gl.getColour()));
                dos.writeUTF(gl.getA().getId().toString());
                dos.writeUTF(gl.getB().getId().toString());
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link GLine} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param pointSuperMap A {@link SuperMap} containing {@link GPoint} objects, used to resolve line endpoints.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link SuperMap} containing the read {@link GLine} objects, mapped by their ID and parent thing ID.
     * @throws IOException If an I/O error occurs or the file is corrupted.
     */
    private static SuperMap<GLine> readLines(DataInputStream dis, SuperMap<GPoint> pointSuperMap, Spinner sp) throws IOException {
        consumeBeginList(dis);
        HashMap<String, GLine> idMap = new HashMap<>();
        HashMultiMap<String, GLine> thingParentMap = new HashMultiMap<>();
        int lineAmt = dis.readInt();
        sp.progressStart("Reading lines", lineAmt);
        for (int i = 0; i < lineAmt; i++) {
            consumeBeforeElement(dis);
            String id = dis.readUTF();
            String parent = dis.readUTF();
            int colour = dis.readInt();
            Color col = intToCol(colour);
            String a = dis.readUTF();
            String b = dis.readUTF();

            GLine line = GLine.fromRaw(
                    id,
                    pointSuperMap.getIdMap().get(a),
                    pointSuperMap.getIdMap().get(b)
            );
            line.setColour(col);
            sp.updateProgress(i + 1);
            idMap.put(id, line);
            thingParentMap.putValue(parent, line);
        }
        consumeEndList(dis);
        return new SuperMap<>(thingParentMap, idMap);
    }

    /**
     * Writes a list of {@link GPoint} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param points An {@link ArrayList} of {@link Pair}s, where each pair contains the parent {@link UUID} and the {@link GPoint} to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePoints(DataOutputStream dos, ArrayList<Pair<UUID, GPoint>> points) throws IOException {
        beginList(dos);
        int pointAmt = points.size();
        dos.writeInt(pointAmt);
        if (!points.isEmpty()) {
            for (Pair<UUID, GPoint> pair : points) {
                beforeElement(dos);
                GPoint gp = pair.second;
                dos.writeUTF(gp.getId().toString());
                UUID parent = pair.first;
                dos.writeUTF(parent.toString());

                dos.writeDouble(gp.getPivot().getX());
                dos.writeDouble(gp.getPivot().getY());
                dos.writeDouble(gp.getPivot().getZ());

                dos.writeInt(colToInt(gp.getColour()));
            }
        }
        endList(dos);
    }

    /**
     * Reads a list of {@link GPoint} objects from the provided {@link DataInputStream}.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @param sp The {@link Spinner} to update progress.
     * @return A {@link SuperMap} containing the read {@link GPoint} objects, mapped by their ID and parent thing ID.
     * @throws IOException If an I/O error occurs or the file is corrupted.
     */
    private static SuperMap<GPoint> readPoints(DataInputStream dis, Spinner sp) throws IOException {
        consumeBeginList(dis);
        HashMap<String, GPoint> idMap = new HashMap<>();
        HashMultiMap<String, GPoint> thingParentMap = new HashMultiMap<>();
        int pointAmt = dis.readInt();
        sp.progressStart("Reading points", pointAmt);
        for (int i = 0; i < pointAmt; i++) {
            consumeBeforeElement(dis);
            String id = dis.readUTF();
            String parent = dis.readUTF();
            double x = dis.readDouble();
            double y = dis.readDouble();
            double z = dis.readDouble();
            int colour = dis.readInt();
            Color col = intToCol(colour);

            GPoint point = GPoint.fromRaw(id, new Vector3(x, y, z));
            point.setColour(col);
            sp.updateProgress(i + 1);
            idMap.put(id, point);
            thingParentMap.putValue(parent, point);
        }
        consumeEndList(dis);
        return new SuperMap<>(thingParentMap, idMap);
    }

    /**
     * Writes a special delimiter to indicate the beginning of a list in the data stream.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void beginList(DataOutputStream dos) throws IOException {
        dos.writeUTF("?");
    }

    /**
     * Consumes and validates the beginning of a list delimiter from the data stream.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @throws IOException If an I/O error occurs or the expected delimiter is not found.
     */
    public static void consumeBeginList(DataInputStream dis) throws IOException {
        String utf = dis.readUTF();
        if (!utf.equals("?")) {
            throw new IOException("Corrupted file: Expected begin list delimiter.");
        }
    }
    /**
     * Writes a special delimiter to indicate the end of a list in the data stream.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void endList(DataOutputStream dos) throws IOException {
        dos.writeUTF("!");
    }

    /**
     * Consumes and validates the end of a list delimiter from the data stream.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @throws IOException If an I/O error occurs or the expected delimiter is not found.
     */
    public static void consumeEndList(DataInputStream dis) throws IOException {
        String utf = dis.readUTF();
        if (!utf.equals("!")) {
            throw new IOException("Corrupted file: Expected end of list delimiter.");
        }
    }
    /**
     * Writes a special delimiter to indicate the start of an element within a list in the data stream.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void beforeElement(DataOutputStream dos) throws IOException {
        dos.writeByte(0x11111111);
    }

    /**
     * Consumes and validates the element delimiter from the data stream.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @throws IOException If an I/O error occurs or the expected delimiter is not found.
     */
    public static void consumeBeforeElement(DataInputStream dis) throws IOException {
        byte b = dis.readByte();
        if (b != (byte) 0x11111111) {
            throw new IOException("Corrupted file: Expected element delimiter.");
        }
    }
    /**
     * Writes a magic number (69) to the data stream for file integrity check.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeSixNine(DataOutputStream dos) throws IOException {
        dos.writeShort(69);
    }

    /**
     * Reads and validates a magic number (69) from the data stream for file integrity check.
     *
     * @param dis The {@link DataInputStream} to read from.
     * @throws IOException If an I/O error occurs or the expected magic number is not found.
     */
    public static void readSixNine(DataInputStream dis) throws IOException {
        short s = dis.readShort();
        if (s != 69) {
            throw new IOException("Corrupted file: Expected 69.");
        }
    }
    /**
     * Logs a message to the application's log output.
     *
     * @param message The message string to be logged.
     */
    private static void msg(String message) {
        StaticRefs.getLog().println(message);
    }

    /**
     * A utility class that extends {@link Pair} to store two maps related to some {@link GObject}.
     * It holds a {@link HashMultiMap} to map parent thing IDs to a list of geometric objects,
     * and a {@link HashMap} to map individual geometric object IDs to the objects themselves.
     * @implNote Rather than calling {@link #first} and {@link #second}, this class
     * allows you to call the convenience methods instead, being {@link #getThingParentMap()}
     * and {@link #getIdMap()} respectively
     *
     * @param <T> The type of {@link GObject} stored in the maps.
     */
    // Named it SuperMAp cuz funny
    public static class SuperMap<T extends GObject> extends Pair<
            HashMultiMap<String, T>,
            HashMap<String, T>
            > {
        /**
         * Constructs a new {@code SuperMap} with the given parent map and ID map.
         *
         * @param thingParentMap A {@link HashMultiMap} mapping parent thing IDs to geometric objects.
         * @param idMap A {@link HashMap} mapping geometric object IDs to geometric objects.
         */
        public SuperMap(HashMultiMap<String, T> thingParentMap, HashMap<String, T> idMap) {
            super(thingParentMap, idMap);
        }
        /**
         * Returns the {@link HashMultiMap} that maps parent thing IDs to geometric objects.
         *
         * @return The parent thing map.
         */
        public HashMultiMap<String, T> getThingParentMap() {
            return first;
        }
        /**
         * Returns the {@link HashMap} that maps geometric object IDs to geometric objects.
         *
         * @return The ID map.
         */
        public HashMap<String, T> getIdMap() {
            return second;
        }
        /**
         * Retrieves a list of geometric objects associated with a specific parent thing ID.
         * If no objects are found for the given thing ID, an empty {@link ArrayList} is returned.
         *
         * @param thingId The ID of the parent thing.
         * @return An {@link ArrayList} of geometric objects belonging to the specified thing.
         */
        public ArrayList<T> fromThing(String thingId) {
            HashMultiMap<String, T> map = getThingParentMap();
            ArrayList<T> values = map.getValues(thingId);
            return values == null ? new ArrayList<>() : values;
        }
    }
}
