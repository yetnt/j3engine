package com.j3d.gen.docs;

import com.j3d.StaticRefs;
import com.j3d.utility.generic.tuple.Pair;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Represents different documentation files available within da arp.
 * Each enum constant corresponds to a specific documentation topic,
 * providing its human-readable label, a unique file identifier,
 * and a reference to the actual documentation file on the file system.
 */
public enum Documentation {

    /**
     * Guide for getting started with the engine.
     */
    GETTING_STARTED("Getting Started", "getting-started"),
    /**
     * Documentation about the engine.
     */
    ABOUT("About", "about"),
    /**
     * Documentation related to editing features.
     */
    EDITING("Editing", "editing"),
    /**
     * Documentation about available commands.
     */
    COMMANDS("Commands", "commands"),
    /**
     * Documentation on mathematical concepts used in the engine.
     */
    MATHS("Mathematics", "maths");
    /**
     * Frequently Asked Questions.
     */
//    FAQ("FAQ", "faq");
    /**
     * List of known issues*
     */
//    KNOWN_ISSUES("Known Issues", "known-issues"),
//
//    TRANSFORM("Command - Transform", "cmd/transform");

    /**
     * Constructs a new Documentation enum constant.
     *
     * @param label The human-readable label for the documentation.
     * @param id    The unique identifier used for the file name (e.g., "about" for "about.j3.md").
     */
    Documentation(String label, String id) {
        this.fileId = id;
        this.label = label;
        this.file = new File(
                StaticRefs.getEngineFiles().docsFolder.getFolder()
                , id + ".j3.md");
    }

    /**
     * A cached map of documentation files, keyed by their file ID.
     * The value is a {@link Pair} where the first element is the label
     * and the second element is the {@link File} object.
     */
    private static LinkedHashMap<String, Pair<String, File>> MAP = null;

    /**
     * Returns a {@link LinkedHashMap} containing all documentation entries,
     * keyed by their file ID. Each value in the map is a {@link Pair}
     * where the first element is the human-readable label and the second
     * element is the actual {@link File} object.
     * The map is lazily initialised and cached.
     *
     * @return A {@link LinkedHashMap} mapping file IDs to a {@link Pair} of label and file.
     */
    public static LinkedHashMap<String, Pair<String, File>> toMap() {
        if (MAP == null) {
            LinkedHashMap<String, Pair<String, File>> map = new LinkedHashMap<>();
            for (Documentation f : Documentation.values()) {
                map.put(f.getFileId(), new Pair<>(f.getLabel(), f.getFile()));
            }
            MAP = map;
        }
        return MAP;
    }

    public static Documentation from(String id) {
        for (Documentation f : Documentation.values()) {
            if (f.getFileId().equals(id))
                return f;
        }
        return null;
    }

    /**
     * Gets the {@link File} object associated with this documentation entry.
     *
     * @return The {@link File} object representing the documentation file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the human-readable label for this documentation entry.
     *
     * @return The label string.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the unique file identifier for this documentation entry.
     * This ID is used to construct the file name.
     *
     * @return The file ID string.
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * The unique identifier for the documentation file (e.g., "about").
     */
    private final String fileId;
    /**
     * The actual {@link File} object pointing to the documentation file.
     */
    private final File file;
    /**
     * The human-readable label for the documentation (e.g., "About").
     */
    private final String label;
}
