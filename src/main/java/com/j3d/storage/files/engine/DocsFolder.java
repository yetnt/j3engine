package com.j3d.storage.files.engine;

import com.j3d.utility.generic.Pair;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class DocsFolder {
    File folder = new File(Objects.requireNonNull(DocsFolder.class.getResource("/docs/")).toURI());
    private final File about;
    private final File intro;
    private final File gs;
    private final LinkedHashMap<String, Pair<String, File>> fileHashMap = new LinkedHashMap<>();

    public DocsFolder() throws URISyntaxException {
        about = new File(folder, "about.j3.md");
        intro = new File(folder, "intro.j3.md");
        gs = new File(folder, "getting-started.j3.md");

        fileHashMap.put("about", new Pair<>("About", about));
        fileHashMap.put("intro", new Pair<>("Intro", intro));
        fileHashMap.put("getting-started", new Pair<>("Getting Started", gs));
    }

    public File getAbout() {
        return about;
    }

    public File getIntro() {
        return intro;
    }

    public File getGs() {
        return gs;
    }

    public LinkedHashMap<String, Pair<String, File>> getFileHashMap() {
        return fileHashMap;
    }
}
