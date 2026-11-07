package com.j3d.storage.files;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GPolygon;
import com.j3d.engine.scene.nodes.geometry.GPolyline;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class Wavefront {
    public static ArrayList<Thing> read(@NotNull File file) {
        ArrayList<ArrayList<GPoint>> vertices = new ArrayList<>(List.of(new ArrayList<>()));
        ArrayList<ArrayList<GPolygon>> faces = new ArrayList<>(List.of(new ArrayList<>()));
        ArrayList<ArrayList<GPolyline>> lns = new ArrayList<>(List.of(new ArrayList<>()));
        ArrayList<String> name = new ArrayList<>();
        AtomicBoolean hasName = new AtomicBoolean(false);

        FilesUtility.readFromFile(file.getAbsolutePath(), lines -> {
            while (lines.hasNextLine()) {
                String line = lines.nextLine();
                String[] parts = line.split(" ");

                if (parts.length > 1) {
                    switch (parts[0]) {
                        case "v" -> readVertex(
                                !hasName.get() ? vertices.getFirst() : vertices.get(name.size()-1),
                                Arrays.copyOfRange(parts, 1, parts.length)
                        );
                        case "f" -> readFace(
                                !hasName.get() ? faces.getFirst() : faces.get(name.size()-1),
                                vertices.stream().flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new)),
                                Arrays.copyOfRange(parts, 1, parts.length)
                        );
                        case "l" -> readLine(
                                !hasName.get() ? lns.getFirst() : lns.get(name.size()-1),
                                vertices.stream().flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new)),
                                Arrays.copyOfRange(parts, 1, parts.length)
                        );
                        case "o" -> {
                            hasName.set(true);
                            name.add(parts[1]);
                            vertices.add(new ArrayList<>());
                            lns.add(new ArrayList<>());
                            faces.add(new ArrayList<>());
                        }
                    }
                }
            }
        });

        if (!hasName.get())
            name.add("Unnamed");

        ArrayList<Thing> things = new ArrayList<>();

        for (int i = 0; i < name.size(); i++) {
            ArrayList<GPolygon> polygons = faces.get(i);
            ArrayList<GPolyline> lines = lns.get(i);
            ArrayList<GPoint> verts = vertices.get(i);
            String nam =  name.get(i);

            things.add(
                    new Thing(
                            StaticRefs.throwIfNull(StaticRefs.getSceneManager())
                                    .usableLayer(),
                            nam
                    )
                            .addObjs(verts.toArray(new GPoint[0]))
                            .addObjs(lines.toArray(new GPolyline[0]))
                            .addObjs(polygons.toArray(new GPolygon[0]))
            );
        }

        return things;
    }

    public static void readVertex(ArrayList<GPoint> vertices, @NotNull String[] args) {
        if (args.length != 3)
            throw new IllegalArgumentException("Vertex does have 3 coordinates?");

        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);

        vertices.add(new GPoint(new Vector3(x, y, z)));
    }

    public static void readLine(ArrayList<GPolyline> polylines, ArrayList<GPoint> vertices, @NotNull String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException("Line less than 2 vertices?");

        ArrayList<GPoint> vs = new ArrayList<>();
        for (String arg : args) {
            int pos = Integer.parseInt(arg) - 1;
            try {
                vs.add(vertices.get(pos));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid vertex index: " + pos);
            }
        }

        polylines.add(new GPolyline(vs));
    }

    public static void readFace(ArrayList<GPolygon> polygons, ArrayList<GPoint> vertices, @NotNull String[] args) {
        if (args.length < 3)
            throw new IllegalArgumentException("Face has less than 3 vertices?");

        ArrayList<GPoint> vs = new ArrayList<>();
        for (String arg : args) {
            String posArg;
            if (arg.contains("/"))
                posArg = arg.substring(0, arg.indexOf("/"));
            else
                posArg = arg;

            int pos = Integer.parseInt(posArg) - 1;

            try {
                vs.add(vertices.get(pos));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid vertex index: " + pos);
            }
        }

        polygons.add(new GPolygon(vs));
    }
}
