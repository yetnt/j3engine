package com.j3d.storage.files.engine;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class DocsFolder {
    public DocsFolder() throws URISyntaxException {
        // Docs live within Documentation enum
    }

    public String getRoot() {
        return "/docs/";
    }
}
