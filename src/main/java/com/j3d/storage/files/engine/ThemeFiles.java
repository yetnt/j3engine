package com.j3d.storage.files.engine;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ThemeFiles {

    private final Path ROOT =  EngineFiles.engineFolder.toPath()
            .resolve("themes");

    public ThemeFiles() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HashMap<String, Object> readTheme(String id) throws IOException {
        
    }
}
