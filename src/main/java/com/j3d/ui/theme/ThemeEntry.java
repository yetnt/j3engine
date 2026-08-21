package com.j3d.ui.theme;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.utility.Parsing;

import java.awt.*;
import java.util.HashMap;

public class ThemeEntry {
    private final String name;
    private HashMap<ThemeKey, Color> entries = new HashMap<>();
    private final ThemeType themeType;

    public ThemeEntry(String name, HashMap<ThemeKey, Color> colorHashMap, ThemeType themeType) {
        if (colorHashMap.size() != 6) {
            throw new RuntimeException("invalid colour hashmap given");
        }
        this.name = name;
        entries = colorHashMap;
        this.themeType = themeType;
    }

    public ThemeEntry(
            String name,
            Color textPrimary,
            Color textSecondary,
            Color accentPrimary,
            Color accentSecondary,
            Color uiSurface,
            Color background,
            ThemeType themeType
    ) {
        this.name = name;
        entries =
                new HashMap<>() {{
                    this.put(ThemeKey.TEXT_PRIMARY, textPrimary);
                    this.put(ThemeKey.TEXT_SECONDARY, textSecondary);
                    this.put(ThemeKey.ACCENT_PRIMARY, accentPrimary);
                    this.put(ThemeKey.ACCENT_SECONDARY, accentSecondary);
                    this.put(ThemeKey.UI_SURFACE, uiSurface);
                    this.put(ThemeKey.BACKGROUND, background);
                }};
        this.themeType = themeType;
    }

    public ThemeEntry(
            String name,
            String textPrimary,
            String textSecondary,
            String accentPrimary,
            String accentSecondary,
            String uiSurface,
            String background,
            ThemeType themeType
    ) {
        this(
                name,
                Color.decode(textPrimary), Color.decode(textSecondary),
                Color.decode(accentPrimary), Color.decode(accentSecondary),
                Color.decode(uiSurface), Color.decode(background),
                themeType
        );
    }

    public String getName() {
        return name;
    }

    public HashMap<ThemeKey, Color> getEntries() {
        return entries;
    }

    public ThemeType getThemeType() {
        return themeType;
    }

    public enum ThemeType {
        LIGHT, DARK, HIGH_CONTRAST, SATURATED, USER_LOADED;

        @Override
        public String toString() {
            return Parsing.toCamelCase(this.name());
        }

        public static ThemeType fromString(String str) {
            StringBuilder s = new  StringBuilder();
            for (char c : str.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    s.append("_");
                }
                s.append(Character.toUpperCase(c));
            }

            return valueOf(s.toString());
        }
    }
}
