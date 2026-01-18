package com.j3d.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DebugDump {

    private static final Path ROOT = Paths.get("debug", "dump");

    static {
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void dump(String name, String content) throws IOException {
        Files.writeString(
                ROOT.resolve(name),
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public static PrintWriter writer(String name) throws IOException {
        return new PrintWriter(
                Files.newBufferedWriter(
                        ROOT.resolve(name),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                )
        );
    }
}
