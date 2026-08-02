package com.j3d.storage.files.engine;

import com.j3d.utility.generic.Pair;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Objects;

public class DocsFolder {
    private final File folder = new File(Objects.requireNonNull(DocsFolder.class.getResource("/docs/")).toURI());;

    public DocsFolder() throws URISyntaxException {
        // Docs live within Documentation enum
    }

    public File getFolder() {
        return folder;
    }
}
