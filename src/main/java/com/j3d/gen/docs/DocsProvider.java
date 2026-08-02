package com.j3d.gen.docs;

import com.j3d.StaticRefs;
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

    /**
     * Provides the main documentation frame.
     * This frame is always available and serves as the primary documentation viewer.
     * @return The main {@link DocsFrame} instance.
     */
    public DocsFrame provideMain() {
        return main;
    }

    /**
     * Provides a {@link DocsFrame} for a given documentation ID.
     * If a frame for the specified ID already exists in the cache, it is returned.
     * Otherwise, a new {@link DocsFrame} is created, initialized with the given ID,
     * added to the cache, and then returned.
     * If the ID does not correspond to an existing documentation file, an error is handled.
     * @param id The unique identifier for the documentation file.
     * @return The {@link DocsFrame} associated with the provided ID.
     */
    public DocsFrame provideDocFrame(String id) {
        DocsFrame f = frameHashMap.get(id);
        if (f == null) {
            if (Documentation.toMap().get(id) == null) {
                StaticRefs.getErrs().handle(
                        new DocsGenException(
                                "Attempt to get help frame for non existent file: " + id
                        ).code(201)
                );
            }
            // create it.
            f = new DocsFrame(id);
            frameHashMap.put(id, f);
        }
        return f;
    }

    /**
     * Displays a specific documentation frame and scrolls to a designated header within it.
     * This method first retrieves or creates a {@link DocsFrame} for the given {@link Documentation} object
     * using its file ID. It then makes the frame visible and instructs it to scroll to the specified header.
     * @param doc The {@link Documentation} object whose frame is to be shown and scrolled.
     * @param header The header string within the documentation to scroll to.
     */
    public void showAndScrollTo(Documentation doc, String header) {
        DocsFrame f = provideDocFrame(doc.getFileId());
        f.setVisible(true);
        f.scrollToHeader(header);
    }

}
