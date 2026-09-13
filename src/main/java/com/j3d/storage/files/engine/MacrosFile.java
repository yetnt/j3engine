package com.j3d.storage.files.engine;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.macros.MacroLine;
import com.j3d.ui.theme.ThemeEntry;
import com.j3d.ui.theme.ThemeKey;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MacrosFile {

    private final Path ROOT =  EngineFiles.engineFolder.toPath()
            .resolve("macros");

    public MacrosFile() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void write(String name, ArrayList<MacroLine> instructions) throws IOException {
        File file = new File(ROOT.toFile(), name + ".j3d.macro");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (PrintWriter s = new PrintWriter(file)) {
            instructions.forEach(s::println);
        }
    }

    public ArrayList<MacroLine> read(String name) throws IOException {
        File file = new File(ROOT.toFile(), name + ".j3d.macro");
        ArrayList<MacroLine> lines = new ArrayList<>();
        if (!file.exists()) return lines;
        try (Scanner s = new Scanner(file)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] split = line.split(":");
                lines.add(MacroLine.read(split[0].trim(), split[1].trim()));
            }
        }
        return lines;
    }
}
