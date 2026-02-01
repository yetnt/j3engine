package com.j3d.ui;

import java.awt.*;

/**
 * The Theme class contains a set of predefined color themes for the UI.
 */
public enum J3DTheme {

    /**
     * Text Colour possibly.
     * <p>
     *     Preview: <div style="background-color:#cad2c5; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    SAGE_GRAY(new Color(0xcad2c5)),
    /**
     * Hover state
     * <p>
     *     Preview: <div style="background-color:#84a98c; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    DUSTY_GREEN(new Color(0x84a98c)),
    /**
     * Borders for test fgields or separators. Structure.
     * <p>
     *     Preview: <div style="background-color:#52796f; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    DEEP_SEA_GREEN(new Color(0x52796f)),
    /**
     * Dark enough for panel backgrounds
     * <p>
     *     Preview: <div style="background-color:#354f52; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    DARK_SLATE(new Color(0x354f52)),
    /**
     * Darkest shade, maybe for scene background.
     * <p>
     *     Preview: <div style="background-color:#2f3e46; width:150px; height:50px; border:1px solid black;"></div>
     * </p>
     */
    CHARCOAL_GREEN(new Color(0x2f3e46));

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
        return col;
    }
}
