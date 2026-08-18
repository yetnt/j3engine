package com.j3d.ui.theme.updator;

import com.j3d.ui.theme.J3DTheme;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Manages the updating of UI component properties based on a theme.
 * It holds a collection of {@link Locator} objects, each linking a specific
 * theme property (represented by {@link J3DTheme}) to a method that sets
 * a UI component's colour property.
 * When {@link #update()} is called, all registered components are updated
 * with their respective theme colours.
 * @see Locator
 * @author Lehlogonolo Poole
 */
public class ThemeUpdater {

    /**
     * A list of {@link Locator} objects, each representing a UI component property
     * that needs to be updated when the theme changes.
     */
    private final ArrayList<Locator> entries = new ArrayList<>();

    /**
     * Constructs a new {@code ThemeUpdater}.
     * Initializes an empty list of entries.
     */
    public ThemeUpdater() {
        // Constructor is intentionally empty as the list is initialized directly.
    }

    /**
     * Removes a collection of {@link Locator} objects from the updater.
     *
     * @param l2 The {@link ArrayList} of {@link Locator} objects to be removed.
     */
    public void remove(ArrayList<Locator> l2) {
        entries.removeAll(l2);
    }

    /**
     * Removes a single {@link Locator} object from the updater.
     *
     * @param l The {@link Locator} object to be removed.
     */
    public void remove(Locator l) {
        entries.remove(l);
    }

    /**
     * Adds a new {@link Locator} to the updater, linking a theme property
     * to a consumer that sets a color property on a UI component.
     *
     * @param themeProperty The {@link J3DTheme} enum value representing the
     *                      specific theme color property to track.
     * @param propertySetter A {@link Consumer} that accepts a {@link Color}
     *                       and applies it to the corresponding UI component property.
     * @return The newly created {@link Locator} object that was added to the updater.
     */
    public Locator add(J3DTheme themeProperty, Consumer<Color> propertySetter) {
        Locator l = new Locator(themeProperty, propertySetter);
        entries.add(l);
        return l;
    }

    /**
     * Iterates through all registered {@link Locator} objects and updates
     * their associated UI component properties with the current color
     * defined by their respective {@link J3DTheme} property.
     * This method should be called whenever the theme changes or when
     * components need to reflect the current theme.
     */
    public void update() {
        for (Locator l : entries) {
            l.propertySetter().accept(l.themeProperty().color());
        }
    }
}
