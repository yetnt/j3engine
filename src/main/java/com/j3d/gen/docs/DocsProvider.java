package com.j3d.gen.docs;

import com.j3d.StaticRefs;
import com.j3d.gen.docs.reader.DocsGenException;
import com.j3d.ui.docs.DocsFrame;

import java.util.HashMap;

/**
 * Provides and manages documentation frames within the J3Engine.
 * This class acts as a central point for accessing and creating {@link DocsFrame} instances,
 * ensuring that documentation files are loaded and displayed efficiently.
 * It maintains a cache of {@link DocsFrame} objects to prevent redundant loading.
 * @author Lehlogonolo Poole
 */
public class DocsProvider {

    /**
     * A map to store {@link DocsFrame} instances, keyed by their ID.
     * This allows for efficient retrieval of existing documentation frames.
     */
    private final HashMap<String, DocsFrame> frameHashMap = new HashMap<>();
    /**
     * The main documentation frame, which is always available.
     */
    private final DocsFrame main = new DocsFrame();

    /**
     * Constructs a new {@code DocsProvider}.
     */
    public DocsProvider() {

    }

    public DocsFrame provideMain() {
        return main;
    }

    public DocsFrame provideDocFrame(String id) {
        DocsFrame f = frameHashMap.get(id);
        if (f == null) {
            if (StaticRefs.getEngineFiles().docsFolder.getFileHashMap().get(id) == null) {
                StaticRefs.getErrs().handle(
                        new DocsGenException(
                                "Attempt to get help frame for non existent file: " + id
                        )
                );
            }
            // create it.
            f = new DocsFrame(id);
            frameHashMap.put(id, f);
        }
        return f;
    }
}
