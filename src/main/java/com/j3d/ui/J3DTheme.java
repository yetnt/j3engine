package com.j3d.ui;

import java.awt.*;
import java.util.HashMap;

import static com.j3d.storage.db.ThemesTable.getTheme;

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
    TEXT_PRIMARY(new Color(0xcad2c5)),
    /**
     * Secondary Text Colour.
     */
    TEXT_SECONDARY(new Color(0xc5e0c6)),
    /**
     * Even lighter colour
     * <p>
     *     Preview: <div style="background-color:#84a98c; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    ACCENT_PRIMARY(new Color(0x84a98c)),
    /**
     * Lighter colour.
     * <p>
     *     Preview: <div style="background-color:#52796f; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    ACCENT_SECONDARY(new Color(0x52796f)),
    /**
     * Dark colour for button or any UI element backgrounds but not too dark
     * <p>
     *     Preview: <div style="background-color:#354f52; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    UI_SURFACE(new Color(0x354f52)),
    /**
     * Darkest shade. Used for panel backgrounds and the entire scene background
     * <p>
     *     Preview: <div style="background-color:#2f3e46; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    BACKGROUND(new Color(0x2f3e46));

    public static final HashMap<String, Color> fromDbTest = getTheme(4).toColorHashMap(); // bubblegum theme

    J3DTheme(Color color) {
        col = color;
    };

    final Color col;


    /**
     * Return the color associated with this theme.
     *
     * @return the {@link Color} instance representing this theme's color
     */
    public Color color() {
        return fromDbTest.getOrDefault(toDbFieldName(), col);
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
}
