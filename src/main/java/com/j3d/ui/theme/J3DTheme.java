package com.j3d.ui.theme;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.themes.ThemesTable;

import java.awt.*;
import java.util.HashMap;


/**
 * The Main Theme enum where all theme properties are accessed
 * <p>
 *     Almost all UI classes use this for consistent theming so changing this will just mean death.
 * </p>
 * @implSpec This, isn't a normal enum. In that it's usually loaded on Startup via {@link com.j3d.Main}
 * with it's properties from {@link ThemesTable} via the {@link DatabaseManager}. Hence the multiple
 * methods to check what the current ID of the theme that is loaded. However if the database cannot run
 * or has no reason to, it defaults to giving the colours of the default theme which ahs an id of
 * {@code 1}.
 * @author Lehlogonolo Poole
 */
public enum J3DTheme {

    /**
     * Base Text Colour.
     */
    TEXT_PRIMARY,
    /**
     * Secondary Text Colour.
     */
    TEXT_SECONDARY,
    /**
     * Even lighter colour
     */
    ACCENT_PRIMARY,
    /**
     * Lighter colour.
     */
    ACCENT_SECONDARY,
    /**
     * Dark colour for panels and other UI elements.
     */
    UI_SURFACE,
    /**
     * Darkest shade. Used for button backgrounds and the entire scene background
     */
    BACKGROUND;

    J3DTheme() {
//        J3DTheme.loadTheme(1);
    }

    /**
     * Return the color associated with this theme.
     *
     * @return the {@link Color} instance representing this theme's color
     */
    public Color color() {
        return colorMap.getOrDefault(toDbFieldName(), Default.from(toDbFieldName()));
    }

    /**
     * Converts names such as TEXT_PRIMARY to textPrimary
     * @return The converted name
     */
    public String toDbFieldName() {
        String input = name();
        StringBuilder sb = new StringBuilder();
        boolean isFirst = false;
        for (char c : input.toCharArray()) {
            if (isFirst && Character.isUpperCase(c)) {
                sb.append(c);
                isFirst = false;
            } else if (c == '_'){
                isFirst = true;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public Color defaultCol() {
        return new Color(0xffffff);
    }

    private static volatile HashMap<String, Color> colorMap = new HashMap<>();
    private static int currentLoadedTheme = 1; // default
    public static int getCurrentLoadedThemeId() {
        return currentLoadedTheme;
    }

    public static Color colorFromMap(J3DTheme key, HashMap<String, Color> c) {
        return c.getOrDefault(key.toDbFieldName(), key.color());
    }

    public static void loadTheme(int id) {
        colorMap = DatabaseManager.tblThemes.themes.stream().filter(
                t -> t.themeId == id // Default theme. Magic number though.
        ).findFirst().get().toColorHashMap();
        currentLoadedTheme = id;
    }

    /**
     * Provides default {@link Color} values for each theme property.
     * These are used when a theme is not loaded from the database or a specific property is missing.
     */
    public static class Default {
        public static Color from(String st) {
            return switch (st) {
                case "textPrimary" ->
                        new Color(0xcad2c5);
                case "textSecondary" ->
                        new Color(0xc5e0c6);
                case "accentPrimary" ->
                        new Color(0x84a98c);
                case "accentSecondary" ->
                        new Color(0x52796f);
                case "uiSurface" ->
                        new Color(0x354f52);
                case "background" ->
                        new Color(0x2f3e46);
                default ->
                        new Color(0xFFFFFF);
            };
        }
    }
}
