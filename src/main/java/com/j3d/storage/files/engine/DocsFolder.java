package com.j3d.storage.files.engine;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class DocsFolder {
    File folder = new File(Objects.requireNonNull(DocsFolder.class.getResource("/docs/")).toURI());
    public final File about;
    public final File intro;


    public DocsFolder() throws URISyntaxException {
        about = new File(folder, "about.j3.md");
        intro = new File(folder, "intro.j3.md");
    }
}
