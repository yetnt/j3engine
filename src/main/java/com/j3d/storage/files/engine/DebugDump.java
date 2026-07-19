package com.j3d.storage.files.engine;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Camera;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DebugDump {

    private final Path ROOT =  EngineFiles.engineFolder.toPath()
            .resolve("dump");

    public DebugDump() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void print(PrintWriter out, long current, Camera cam) {
        out.println("time,cx,cy,cz,cpitch,cyaw,croll,layerID,layerVisible,thingName,thingID,triID,tridist,trix,triy,triz,trinx,triny,trinz,tricol,triVisible");

        StaticRefs.sceneManager.layers.forEach(
                l -> l.forEach(thing -> thing.getObjects().stream()
                        .filter(GTri.class::isInstance)
                        .map(GTri.class::cast)
                        .forEach(tri -> {
                            String sb = current + "," +
                                    cam.getPosition().getX() + "," +
                                    cam.getPosition().getY() + "," +
                                    cam.getPosition().getZ() + "," +
                                    cam.getRotation().getPitch() + "," +
                                    cam.getRotation().getYaw() + "," +
                                    cam.getRotation().getRoll() + "," +
                                    l.getIdentifier() + "," +
                                    !l.isHidden() + "," +
                                    thing.getName() + "," +
                                    thing.getId() + "," +
                                    tri.getId() + "," +
                                    tri.euclideanDist() + "," +
                                    tri.getPivot().getX() + "," +
                                    tri.getPivot().getY() + "," +
                                    tri.getPivot().getZ() + "," +
                                    tri.normal().getX() + "," +
                                    tri.normal().getY() + "," +
                                    tri.normal().getZ() + "," +
                                    String.format("#%02X%02X%02X", tri.getColour().getRed(), tri.getColour().getGreen(), tri.getColour().getBlue()) + "," +
                                    !tri.isHidden();

                                    out.println(sb);
                                    out.flush();
                        }
                        )
                )
        );
    }

    public void dump(String name, String content) throws IOException {
        Files.writeString(
                ROOT.resolve(name),
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public PrintWriter writer(String name) throws IOException {
        return new PrintWriter(
                Files.newBufferedWriter(
                        ROOT.resolve(name),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                ), true
        );
    }

    public File getFolder() {
        return ROOT.toFile();
    }
}
