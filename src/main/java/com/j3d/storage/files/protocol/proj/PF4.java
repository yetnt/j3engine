package com.j3d.storage.files.protocol.proj;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.SceneObject;
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
import com.yetnt.utils.collection.HashMultiMap;
import com.yetnt.utils.tuple.Pair;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public final class PF4 extends ProjectFile {

    public PF4() {
        super(4,
                Set.of(),     // Can convert to
                Set.of(PF.V3)  // Can convert from
        );
    }

    @Override
    public <T extends ArrayList> void writeFile(String path, String name, T data) {
        Consumer<DataOutputStream> fileWriter = dos -> {
            try {
                writeHeader(dos); // Write J3D file header
                getHeaderWriter().accept(dos); // Write PROJECT file header

                LayerList layers = (LayerList) StaticRefs.throwIfNull(StaticRefs.getSceneManager()).layers.clone();

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
                                .code(400)
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

        StaticRefs.throwIfNull(StaticRefs.getSceneManager()).layers.removeIf(l -> !Objects.equals(l.getName(), Layer.BACKGROUND_ID));


        IOSupplier<DataInputStream> fileReader = dis -> {
            try {
                readHeader(dis);
                getHeaderReader().accept(dis, 4);

                HashMap<String, Layer> layers = readLayers(dis, throbber);
//                layers.put("Three Tris", new Layer("Three Tris")); // fix file
//                layers.put("Cube", new Layer("Cube"));
                sceneObjectLists.addAll(layers.values());

                final SuperMap<GPoint> points = readPoints(dis, throbber);
                final SuperMap<GLine> lines = readLines(dis, points, throbber);
                final SuperMap<GTri> tris = readTris(dis, lines, points, throbber);
                final SuperMap<GCurve> curves = readCurves(dis, points, throbber);

                HashMap<UUID, Thing> things = readThings(dis, layers, throbber);
                sceneObjectLists.addAll(things.values());

                things.forEach(
                        (s, t) -> t
                                .addObjs(points.fromThing(s).toArray(GPoint[]::new))
                                .addObjs(lines.fromThing(s).toArray(GLine[]::new))
                                .addObjs(tris.fromThing(s).toArray(GTri[]::new))
                                .addObjs(curves.fromThing(s).toArray(GCurve[]::new))
                );

                throbber.setTaskTitle("Finalizing");

                StaticRefs.throwIfNull(StaticRefs.getSceneManager()).layers.swingAddAll(layers.values());

                readSixNine(dis);

                msg("Project file of V4 loaded successfully from " + path);
            } catch (IOException e) {
                        StaticRefs.getErrs().handle(
                                new ProjectFileException("Error reading project file data.", e)
                                .code(450)
                        );
            }
            return null;
        };

        FilesUtility.readBinary(path, name, fileReader);
        StaticConfig.hasSaved = true;
        StaticRefs.getEngineFiles().recents.writeProj(new File(path).toPath().resolve(name).toFile());

        return (T) sceneObjectLists;
    }

    private static void writeUUID(DataOutputStream dos, UUID id) throws IOException {
        dos.writeLong(id.getMostSignificantBits());
        dos.writeLong(id.getLeastSignificantBits());
    }

    public static UUID readUUID(DataInputStream dis) throws IOException {
        long most = dis.readLong();
        long least = dis.readLong();
        return new UUID(most, least);
    }

    /**
     * Writes a list of {@link Layer} objects to the provided {@link DataOutputStream}.
     *
     * @param dos The {@link DataOutputStream} to write to.
     * @param layers The {@link LayerList} containing the layers to write.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeLayers(DataOutputStream dos, LayerList layers) throws IOException {
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
        consumeBeginList(dis, "layers");
        int size = dis.readInt();
        sp.progressStart("Reading layers", size);
        for (int i = 0; i < size; i++) {
            consumeBeforeElement(dis, "layers" , i);
            String name = dis.readUTF();
            boolean hidden = dis.readBoolean();
            sp.updateProgress(i + 1);

            layers.put(name, Layer.fromRaw(name, hidden));
        }
        consumeEndList(dis, "layers");
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
                Thing thing = pair.getSecond();
                beforeElement(dos);
                writeUUID(dos, thing.getId());
                dos.writeUTF(thing.getName());
                dos.writeUTF(pair.getFirst());
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
    private static HashMap<UUID, Thing> readThings(DataInputStream dis, HashMap<String, Layer> layers, Spinner sp) throws IOException {
        consumeBeginList(dis, "things");
        HashMap<UUID, Thing> out = new HashMap<>();
        int thingAmt = dis.readInt();
        sp.progressStart("Reading things", thingAmt);
        for (int i = 0; i < thingAmt; i++) {
            consumeBeforeElement(dis, "things", i);
            UUID id = readUUID(dis);
            String name = dis.readUTF();
            String parentLayerName = dis.readUTF();
            boolean isHidden = dis.readBoolean();
            boolean isSolid = dis.readBoolean();

            Layer l = layers.get(parentLayerName);

            ifAnyNull(
                    "thing", id, "layer", 365, l
            );

            Thing t =
                    Thing.fromRaw(
                            name, id.toString(), isHidden,
                            l
                    );
            if (isSolid)
                t.solidify();
            sp.updateProgress(i + 1);
            out.put(id, t);
        }
        consumeEndList(dis, "things");
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
                GCurve gc = pair.getSecond();
                writeUUID(dos, gc.getId());
                UUID parent = pair.getFirst();
                writeUUID(dos, parent);
                dos.writeInt(colToInt(gc.getColour()));
                dos.writeInt(gc.getAmount());
                writeUUID(dos, gc.getStart().getId());
                writeUUID(dos, gc.getControlPoint().getId());
                writeUUID(dos, gc.getEnd().getId());
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
        consumeBeginList(dis, "curves");
        HashMap<UUID, GCurve> idMap = new HashMap<>();
        HashMultiMap<UUID, GCurve> thingParentMap = new HashMultiMap<>();
        int curveAmt = dis.readInt();
        sp.progressStart("Reading curves", curveAmt);
        for (int i = 0; i < curveAmt; i++) {
            consumeBeforeElement(dis, "curves", i);
            UUID id = readUUID(dis);
            UUID parent = readUUID(dis);
            int colour = dis.readInt();
            Color col = intToCol(colour);
            int amount = dis.readInt();
            UUID start = readUUID(dis);
            UUID control = readUUID(dis);
            UUID end = readUUID(dis);
            GPoint A = pointSuperMap.getIdMap().get(start);
            GPoint B = pointSuperMap.getIdMap().get(control);
            GPoint C = pointSuperMap.getIdMap().get(end);

            ifAnyNull(
                    "curve", id, "point", 364, A, B, C
            );

            GCurve curve = GCurve.fromRaw(id.toString(), A, B, C, col, amount);
            sp.updateProgress(i + 1);
            idMap.put(id, curve);
            thingParentMap.putValue(parent, curve);
        }
        consumeEndList(dis, "curves");
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
                GTri gt = pair.getSecond();
                writeUUID(dos, gt.getId());
                UUID parent = pair.getFirst();
                writeUUID(dos, parent);
                dos.writeInt(colToInt(gt.getColour()));

                writeUUID(dos, gt.getLegA().getId());
                writeUUID(dos, gt.getLegB().getId());
                writeUUID(dos, gt.getLegC().getId());

                writeUUID(dos, gt.getWinding().first().getId());
                writeUUID(dos, gt.getWinding().second().getId());
                writeUUID(dos, gt.getWinding().third().getId());

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
        consumeBeginList(dis, "tris");
        HashMap<UUID, GTri> idMap = new HashMap<>();
        HashMultiMap<UUID, GTri> thingParentMap = new HashMultiMap<>();
        int triAmt = dis.readInt();
        sp.progressStart("Reading tris", triAmt);
        for (int i = 0; i < triAmt; i++) {
            consumeBeforeElement(dis, "tris", i);
            UUID id = readUUID(dis);
            UUID parent = readUUID(dis);
            int colour = dis.readInt();
            Color col = intToCol(colour);
            UUID legA = readUUID(dis); GLine LegA = lines.getIdMap().get(legA);
            UUID legB = readUUID(dis); GLine LegB = lines.getIdMap().get(legB);
            UUID legC = readUUID(dis); GLine LegC = lines.getIdMap().get(legC);
            UUID w1 = readUUID(dis); GPoint p1 = points.getIdMap().get(w1);
            UUID w2 = readUUID(dis); GPoint p2 = points.getIdMap().get(w2);
            UUID w3 = readUUID(dis); GPoint p3 = points.getIdMap().get(w3);
            boolean isDoubleSided = dis.readBoolean();

            ifAnyNull(
                    "tri", id, "line", 362, LegA, LegB, LegC
            );
            ifAnyNull(
                    "tri", id, "winding point", 363, p1, p2, p3
            );

            GTri tri = GTri.fromV3Raw(
                    id.toString(), col,
                    LegA, LegB, LegC,
                    p1, p2, p3
            );
            tri.setDoubleSided(isDoubleSided);
            sp.updateProgress(i + 1);
            idMap.put(id, tri);
            thingParentMap.putValue(parent, tri);
        }
        consumeEndList(dis, "tris");
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
                GLine gl = pair.getSecond();
                writeUUID(dos, gl.getId());
                UUID parent = pair.getFirst();
                writeUUID(dos, parent);
                dos.writeInt(colToInt(gl.getColour()));
                writeUUID(dos, gl.getA().getId());
                writeUUID(dos, gl.getB().getId());
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
        consumeBeginList(dis, "lines");
        HashMap<UUID, GLine> idMap = new HashMap<>();
        HashMultiMap<UUID, GLine> thingParentMap = new HashMultiMap<>();
        int lineAmt = dis.readInt();
        sp.progressStart("Reading lines", lineAmt);
        for (int i = 0; i < lineAmt; i++) {
            consumeBeforeElement(dis, "lines", i);
            UUID id = readUUID(dis);
            UUID parent = readUUID(dis);
            int colour = dis.readInt();
            Color col = intToCol(colour);
            UUID a = readUUID(dis);
            UUID b = readUUID(dis);

            GPoint A = pointSuperMap.getIdMap().get(a);
            GPoint B = pointSuperMap.getIdMap().get(b);

            ifAnyNull(
                    "lines", id, "points", 361, A, B
            );

            GLine line = GLine.fromRaw(
                    id.toString(),
                    A,
                    B
            );
            line.setColour(col);
            sp.updateProgress(i + 1);
            idMap.put(id, line);
            thingParentMap.putValue(parent, line);
        }
        consumeEndList(dis, "lines");
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
                GPoint gp = pair.getSecond();
                writeUUID(dos, gp.getId());
                UUID parent = pair.getFirst();
                writeUUID(dos, parent);

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
        consumeBeginList(dis, "points");
        HashMap<UUID, GPoint> idMap = new HashMap<>();
        HashMultiMap<UUID, GPoint> thingParentMap = new HashMultiMap<>();
        int pointAmt = dis.readInt();
        sp.progressStart("Reading points", pointAmt);
        for (int i = 0; i < pointAmt; i++) {
            consumeBeforeElement(dis, "points", i);
            UUID id = readUUID(dis);
            UUID parent = readUUID(dis);
            double x = dis.readDouble();
            double y = dis.readDouble();
            double z = dis.readDouble();
            int colour = dis.readInt();
            Color col = intToCol(colour);

            GPoint point = GPoint.fromRaw(id.toString(), new Vector3(x, y, z));
            point.setColour(col);
            sp.updateProgress(i + 1);
            idMap.put(id, point);
            thingParentMap.putValue(parent, point);
        }
        consumeEndList(dis, "points");
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
    public static void consumeBeginList(DataInputStream dis, String errStr) throws IOException {
        String utf = dis.readUTF();
        if (!utf.equals("?")) {
           StaticRefs.getErrs().handle(
                   ProjectFileException.corrupted(
                           "PF3 (at "+errStr+") expected the begin list delimiter. Instead got " + utf + ".")
                           .code(451)
           );
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
    public static void consumeEndList(DataInputStream dis, String errStr) throws IOException {
        String utf = dis.readUTF();
        if (!utf.equals("!")) {
            StaticRefs.getErrs().handle(
                            ProjectFileException.corrupted("PF3 (at "+errStr+") expected the end of list delimiter but got " + utf + ".")
                            .code(452)
            );
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
    public static void consumeBeforeElement(DataInputStream dis, String errStr, int index) throws IOException {
        byte b = dis.readByte();
        if (b != (byte) 0x11111111) {
            StaticRefs.getErrs().handle(
                    ProjectFileException.corrupted("PF3 (at "+errStr+"["+index+"]) expected the before element delimiter but got " + b + ".")
                            .code(453)
            );
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
            StaticRefs.getErrs().handle(
                    ProjectFileException.corrupted("PF3 expected the end of file magic number (69) but got " + s + ".")
                            .code(454)
            );
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
     * Checks if any of the provided {@link SceneObject}s are null. If a null object is found,
     * it handles a {@link ProjectFileException} indicating a corrupted file.
     * This is typically used during file reading to ensure all referenced objects exist.
     *
     * @param errStr A string describing the type of object being checked (e.g., "tri", "curve").
     * @param id The ID of the main object that references the potentially null objects.
     * @param label A string describing the type of referenced object (e.g., "point", "line").
     * @param code The error code to associate with the {@link ProjectFileException}.
     * @param objs A variable argument list of {@link SceneObject}s to check for null.
     */
    private static void ifAnyNull(String errStr, UUID id,  String label, int code, SceneObject...objs) {
        for (SceneObject o : objs) {
            if (o == null) {
                StaticRefs.getErrs().handle(
                        ProjectFileException.corrupted("PF4 could not find the " + label + " referenced by the current "
                                + errStr + " (" + id + ")")
                                .code(code)
                );
            }
        }
    }


    private static class SuperMap<T extends GObject> extends
            com.j3d.storage.files.protocol.proj.SuperMap<UUID, T> {
        /**
         * Constructs a new {@code SuperMap} with the given parent map and ID map.
         *
         * @param thingParentMap A {@link HashMultiMap} mapping parent thing IDs to geometric objects.
         * @param idMap          A {@link HashMap} mapping geometric object IDs to geometric objects.
         */
        public SuperMap(HashMultiMap<UUID, T> thingParentMap, HashMap<UUID, T> idMap) {
            super(thingParentMap, idMap);
        }
    }

}
