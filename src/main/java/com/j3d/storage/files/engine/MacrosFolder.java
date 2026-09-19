package com.j3d.storage.files.engine;

import com.j3d.engine.interact.macros.MacroLine;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MacrosFolder {

    private final Path ROOT =  EngineFiles.engineFolder.toPath()
            .resolve("macros");
    private File macroKeysMapFile;

    public Path getROOT() {
        return ROOT;
    }

    public MacrosFolder() throws IOException {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        macroKeysMapFile = new File(ROOT.toFile(), "keys.j3d.kmap");
        if (!macroKeysMapFile.exists()) {
            macroKeysMapFile.createNewFile();
        }
    }

    public void writeKeysMap(HashMap<KeyStroke, String> map) throws FileNotFoundException {
        try (PrintWriter s = new PrintWriter(macroKeysMapFile)) {
            map.forEach((s1, s2) -> {
                s.println(s2 + "=" + s1.getKeyCode() + "-" + s1.getModifiers());
            });
        }
    }

    public HashMap<KeyStroke, String> readKeysMap() throws FileNotFoundException {
        HashMap<KeyStroke, String> map = new HashMap<>();
        try (Scanner s = new Scanner(macroKeysMapFile)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] parts = line.split("=");
                String name = parts[0].trim();

                String[] parts2 =  parts[1].split("-");
                int keyCode = Integer.parseInt(parts2[0].trim());
                int modifiers = Integer.parseInt(parts2[1].trim());
                // noinspection MagicConstant
                KeyStroke k = KeyStroke.getKeyStroke(
                        keyCode, modifiers
                );

                map.put(k, name);
            }
        }
        return map;
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

    public HashMap<String, ArrayList<MacroLine>> readAll() throws IOException {
        File[] files = ROOT.toFile().listFiles();
        if (files == null) return new HashMap<>();
        HashMap<String, ArrayList<MacroLine>> map = new HashMap<>();
        for (File file : files) {
            if (!file.getName().endsWith(".j3d.macro")) continue;
            String fileName = file.getName().replace(".j3d.macro", "");
            ArrayList<MacroLine> lines = read(fileName);
            map.put(fileName, lines);
        }
        return map;
    }
}
