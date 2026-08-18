package com.j3d.ui.theme;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.themes.ThemesTable;
import com.j3d.ui.theme.updator.Locator;
import com.j3d.ui.theme.updator.ThemeUpdater;
import com.j3d.utility.Parsing;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


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
        return colorMap.getOrDefault(Parsing.toCamelCase(name()), Default.from(Parsing.toCamelCase(name())));
    }

    public static Color transparency(J3DTheme col, int alpha) {
        Color c = col.color();
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public Color defaultCol() {
        return new Color(0xffffff);
    }

    public static final ThemeUpdater themeUpdater = new ThemeUpdater();
    public static Locator commit(J3DTheme themeProperty, Consumer<Color> propertySetter) {
        return themeUpdater.add(themeProperty, propertySetter);
    }
    public static Locator commitAsGenericUi(Component component) {
        return themeUpdater.add(
                J3DTheme.UI_SURFACE,
                component::setBackground
        );
    }
    public static ArrayList<Locator> commitAsGenericLbl(Component component, boolean setBackground) {
        ArrayList<Locator> locators = new ArrayList<>();
        locators.add(themeUpdater.add(
                J3DTheme.TEXT_PRIMARY,
                component::setForeground
        ));
        if (setBackground)
            locators.add(themeUpdater.add(
                    J3DTheme.BACKGROUND,
                    component::setBackground
            ));
        return locators;
    }

    private static volatile HashMap<String, Color> colorMap = new HashMap<>();
    private static int currentLoadedTheme = 1; // default
    public static int getCurrentLoadedThemeId() {
        return currentLoadedTheme;
    }

    public static Color colorFromMap(J3DTheme key, HashMap<String, Color> c) {
        return c.getOrDefault(Parsing.toCamelCase(key.name()), key.color());
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
