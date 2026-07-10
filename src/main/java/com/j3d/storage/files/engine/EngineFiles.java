package com.j3d.storage.files.engine;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class EngineFiles {
    public static File engineFolder = Path.of(System.getProperty("user.home"), "J3Engine").toFile();
    public RecentProjectsFile recents;
    public PinnedProjectsFile pinned;
    public LogFile logFile;
    public DebugDump debugDump;
    public DocsFolder docsFolder;


    public EngineFiles() {
        if (!engineFolder.exists()) {
            engineFolder.mkdirs();
        }
        recents = new RecentProjectsFile();
        pinned = new PinnedProjectsFile();
        logFile = new LogFile();
        debugDump = new DebugDump();
        try {
            docsFolder = new DocsFolder();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
