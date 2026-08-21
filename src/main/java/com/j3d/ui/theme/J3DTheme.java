package com.j3d.ui.theme;

import com.j3d.ui.theme.updator.Locator;
import com.j3d.ui.theme.updator.ThemeUpdater;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * The Main Theme enum where all theme properties are accessed
 * <p>
 *     Almost all UI classes use this for consistent theming so changing this will just mean death.
 * </p>
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
        return getCurrentLoadedTheme().getEntries().getOrDefault(
                ThemeKey.valueOf(this.name()),
                DefaultThemes.DEFAULT.getThemeEntry().getEntries().get(ThemeKey.valueOf(this.name()))
        );
    }

    public static Color transparency(J3DTheme col, int alpha) {
        Color c = col.color();
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
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

    private static ThemeEntry currentLoadedTheme = DefaultThemes.DEFAULT.getThemeEntry(); // default
    public static ThemeEntry getCurrentLoadedTheme() {
        return currentLoadedTheme;
    }

    public static void loadTheme(ThemeEntry themeEntry) {
        currentLoadedTheme = themeEntry;
    }

    public static void loadTheme() {
        loadTheme(DefaultThemes.DEFAULT.getThemeEntry());
    }
}
