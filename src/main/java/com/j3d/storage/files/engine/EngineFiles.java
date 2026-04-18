package com.j3d.storage.files.engine;

import java.io.File;
import java.nio.file.Path;

public class EngineFiles {
    public static File engineFolder = Path.of(System.getProperty("user.home"), "J3Engine").toFile();
    public RecentProjectsFile recents;
    public PinnedProjectsFile pinned;

    public EngineFiles() {
        if (!engineFolder.exists()) {
            engineFolder.mkdirs();
        }
        recents = new RecentProjectsFile();
        pinned = new PinnedProjectsFile();
    }
}
