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
    private final File editing;
    private final File commands;
    private final File maths;
    private final File errors;
    private final File faq;
    private final File knownIssues;

    private final LinkedHashMap<String, Pair<String, File>> fileHashMap = new LinkedHashMap<>();

    public DocsFolder() throws URISyntaxException {
        about = new File(folder, "about.j3.md");
        intro = new File(folder, "intro.j3.md");
        editing = new File(folder, "editing.j3.md");
        commands = new File(folder, "commands.j3.md");
        maths = new File(folder, "maths.j3.md");
        errors = new File(folder, "errors.j3.md");
        faq = new File(folder, "faq.j3.md");
        knownIssues = new File(folder, "known-issues.j3.md");
        gs = new File(folder, "getting-started.j3.md");

        fileHashMap.put("about", new Pair<>("About", about));
        fileHashMap.put("intro", new Pair<>("Intro", intro));
        fileHashMap.put("getting-started", new Pair<>("Getting Started", gs));
        fileHashMap.put("editing", new Pair<>("Editing", editing));
        fileHashMap.put("commands", new Pair<>("Commands", commands));
        fileHashMap.put("maths", new Pair<>("Mathematics", maths));
        fileHashMap.put("errors", new Pair<>("Errors", errors));
        fileHashMap.put("faq", new Pair<>("FAQ", faq));
        fileHashMap.put("known-issues", new Pair<>("Known Issues", knownIssues));
    }

    public LinkedHashMap<String, Pair<String, File>> getFileHashMap() {
        return fileHashMap;
    }
}
