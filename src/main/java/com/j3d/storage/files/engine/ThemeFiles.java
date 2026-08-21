package com.j3d.storage.files.engine;

import com.j3d.engine.interact.cmd.CommandParser;
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

public class ThemeFiles {

    private final Path ROOT =  EngineFiles.engineFolder.toPath()
            .resolve("themes");

    public ThemeFiles() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectories(ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeTheme(String id, ThemeEntry entry) throws IOException {
        if (entry.getThemeType() != ThemeEntry.ThemeType.USER_LOADED)
            return;

        HashMap<String, Object> keyValues = new HashMap<>();
        keyValues.put("name", entry.getName());
        entry.getEntries().forEach((k, v) -> {
            keyValues.put(
                    k.toString(),
                    String.format("#%02X%02X%02X%02X",
                            v.getRed(),
                            v.getGreen(),
                            v.getBlue(),
                            v.getAlpha()
                    )
            );
        });
        write(id, keyValues);
    }

    private void write(String id, HashMap<String, Object> keyValues) throws IOException {
        File file = new File(ROOT.toFile(), id + ".j3d.theme");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (PrintWriter s = new PrintWriter(file)) {
            keyValues.forEach((k, v) -> {
                s.println(k + "=" + v);
            });
        }
    }

    private HashMap<String, String> read(String id) throws IOException {
        File file = new File(ROOT.toFile(), id + ".j3d.theme");
        HashMap<String, String> keyValues = new HashMap<>();
        if (!file.exists()) return keyValues;
        try (Scanner s = new Scanner(file)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] parts = line.split("=");
                keyValues.put(parts[0], parts[1]);
            }
        }
        return keyValues;
    }

    public ThemeEntry getTheme(String id) throws IOException {
        HashMap<String, String> keyValues = read(id);
        if (keyValues.isEmpty()) return null;

        String name = keyValues.get("name");
        Color textPrimary = readOrReadWithAlpha(keyValues.get(ThemeKey.TEXT_PRIMARY.toString()));
        Color textSecondary = readOrReadWithAlpha(keyValues.get(ThemeKey.TEXT_SECONDARY.toString()));
        Color accentPrimary = readOrReadWithAlpha(keyValues.get(ThemeKey.ACCENT_PRIMARY.toString()));
        Color accentSecondary = readOrReadWithAlpha(keyValues.get(ThemeKey.ACCENT_SECONDARY.toString()));
        Color uiSurface = readOrReadWithAlpha(keyValues.get(ThemeKey.UI_SURFACE.toString()));
        Color background = readOrReadWithAlpha(keyValues.get(ThemeKey.BACKGROUND.toString()));

        return new ThemeEntry(
                name, textPrimary, textSecondary, accentPrimary, accentSecondary, uiSurface, background,
                ThemeEntry.ThemeType.USER_LOADED
        );
    }

    private Color readOrReadWithAlpha(String input) {
        if (input.startsWith("#"))
            input = input.substring(1); // remove #
        if (input.length() != 6 && input.length() != 8)
            throw new IllegalArgumentException("Invalid colour: #" + input);

        int r = Integer.parseInt(input.substring(0, 2), 16);
        int g = Integer.parseInt(input.substring(2, 4), 16);
        int b = Integer.parseInt(input.substring(4, 6), 16);

        if (input.length() == 8) {
            int a = Integer.parseInt(input.substring(6, 8), 16);
            return new Color(r, g, b, a);
        }

        return new Color(r, g, b);
    }

    public HashMap<String, ThemeEntry> loadAllEntries() {
        File[] files = ROOT.toFile().listFiles();
        if (files == null) return new HashMap<>();
        HashMap<String, ThemeEntry> entries = new HashMap<>();
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (file.getPath().endsWith(".j3d.theme")) {
                String id = file.getName().substring(0, file.getName().indexOf(".j3d.theme"));
                ThemeEntry entry = null;
                try {
                    entry = getTheme(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (entry == null) {
                    System.out.println("null entry");
                    continue;
                }
                entries.put(id, entry);
            }
        }
        return entries;
    }
}
