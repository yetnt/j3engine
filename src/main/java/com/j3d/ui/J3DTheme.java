package com.j3d.ui;

import com.j3d.storage.db.DatabaseManager;

import java.awt.*;
import java.util.HashMap;


/**
 * The Theme class contains a set of predefined color themes for the UI.
 */
public enum J3DTheme {

    /**
     * Text Colour.
     * <p>
     *     Preview: <div style="background-color:#cad2c5; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    TEXT_PRIMARY,
    /**
     * Secondary Text Colour.
     */
    TEXT_SECONDARY,
    /**
     * Even lighter colour
     * <p>
     *     Preview: <div style="background-color:#84a98c; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    ACCENT_PRIMARY,
    /**
     * Lighter colour.
     * <p>
     *     Preview: <div style="background-color:#52796f; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    ACCENT_SECONDARY,
    /**
     * Dark colour for button or any UI element backgrounds but not too dark
     * <p>
     *     Preview: <div style="background-color:#354f52; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    UI_SURFACE,
    /**
     * Darkest shade. Used for panel backgrounds and the entire scene background
     * <p>
     *     Preview: <div style="background-color:#2f3e46; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
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
        return colorMap.getOrDefault(toDbFieldName(), new Color(0xffffff));
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

//    static {
//        loadTheme(1);
//    }
}
