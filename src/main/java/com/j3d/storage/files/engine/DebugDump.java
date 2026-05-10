package com.j3d.storage.files.engine;

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
}
