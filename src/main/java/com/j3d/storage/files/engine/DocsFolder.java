package com.j3d.storage.files.engine;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class DocsFolder {
    File folder = new File(Objects.requireNonNull(DocsFolder.class.getResource("/doc/")).toURI());

    public DocsFolder() throws URISyntaxException {

    }
}
