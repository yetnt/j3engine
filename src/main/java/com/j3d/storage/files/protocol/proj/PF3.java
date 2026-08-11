package com.j3d.storage.files.protocol.proj;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GCurve;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.scene.nodes.layer.LayerList;
import com.j3d.storage.errs.ProjectFileException;
import com.j3d.storage.files.FilesUtility;
import com.j3d.utility.generic.Pair;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

                layers.removeFirst(); // Remove "BACKG" layer
                layers.removeIf(Layer::isForDeletion); // Remove layers that are deleted in memory

                // LAYERS
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

                // GET EVERYTHING


                ArrayList<Pair<UUID, GPoint>> points = new ArrayList<>();
                ArrayList<Pair<UUID, GLine>> lines = new ArrayList<>();
                ArrayList<Pair<UUID, GTri>> tris = new ArrayList<>();
                ArrayList<Pair<UUID, GCurve>> curves = new ArrayList<>();
                ArrayList<Pair<String, Thing>> things = new ArrayList<>();

                layers.forEach(layer -> layer.forEach(thing -> {
                            things.add(new Pair<>(thing.getName(), thing));
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

                // LINES
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

                // TRIS
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
                    }
                }
                endList(dos);

                // CURVES
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
                        dos.writeUTF(gc.getStart().getId().toString());
                        dos.writeUTF(gc.getControlPoint().getId().toString());
                        dos.writeUTF(gc.getEnd().getId().toString());
                    }
                }
                endList(dos);

                // THINGS
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

    public void beginList(DataOutputStream dos) throws IOException {
        dos.writeUTF("?");
    }

    public void endList(DataOutputStream dos) throws IOException {
        dos.writeUTF("!");
    }

    public void beforeElement(DataOutputStream dos) throws IOException {
        dos.writeByte(0x11111111);
    }

    public void writeSixNine(DataOutputStream dos) throws IOException {
        dos.writeShort(69);
    }

    private static void msg(String message) {
        StaticRefs.getLog().println(message);
    }

    public static int colToInt(Color col) {
        return col.getRGB();
    }

    public static Color intToCol(int col) {
        return new Color(col, true);
    }
}
