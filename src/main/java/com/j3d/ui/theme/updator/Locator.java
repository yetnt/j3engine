package com.j3d.ui.theme.updator;

import com.j3d.ui.theme.J3DTheme;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A utility class that acts as a locator for a specific theme property and its corresponding
 * setter function. This class is typically used in UI theme management systems to associate
 * a theme property (e.g., a color defined in {@link J3DTheme}) with a method that can
 * apply that colour to a UI component.
 * <p>
 *      It provides methods to access and modify the associated theme property and its setter.
 * </p>
 * @see J3DTheme
 * @see ThemeUpdater
 * @author Lehlogonolo Poole
 */
public final class Locator {
    /**
     * The {@link J3DTheme} enum value representing the specific theme property
     * this locator is associated with.
     */
    private J3DTheme themeProperty;
    /**
     * A {@link Consumer} that accepts a {@link Color} object and applies it
     * to the UI component or property this locator manages.
     */
    private Consumer<Color> propertySetter;

    /**
     * Constructs a new {@code Locator} with the specified theme property and setter.
     *
     * @param themeProperty The {@link J3DTheme} enum value representing the theme property.
     * @param propertySetter A {@link Consumer} that will be used to set the color
     *                       associated with the {@code themeProperty}.
     */
    public Locator(
            J3DTheme themeProperty,
            Consumer<Color> propertySetter
    ) {
        this.themeProperty = themeProperty;
        this.propertySetter = propertySetter;
    }

    /**
     * Returns the {@link J3DTheme} enum value associated with this locator.
     *
     * @return The theme property.
     */
    public J3DTheme themeProperty() {
        return themeProperty;
    }

    /**
     * Returns the {@link Consumer} responsible for setting the color property.
     * This consumer takes a {@link Color} object as input.
     *
     * @return The consumer function for setting the color property.
     */
    public Consumer<Color> propertySetter() {
        return propertySetter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Locator) obj;
        return Objects.equals(this.themeProperty, that.themeProperty) &&
                Objects.equals(this.propertySetter, that.propertySetter);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(themeProperty, propertySetter);
    }

    /**
     * Returns a string representation of this {@code Locator}.
     * The string representation includes the theme property and the property setter.
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return "Locator[" +
                "themeProperty=" + themeProperty + ", " +
                "propertySetter=" + propertySetter + ']';
    }

    /**
     * Sets the {@link Consumer} responsible for setting the color property.
     *
     * @param propertySetter The new consumer function for setting the color property.
     */
    public void setPropertySetter(Consumer<Color> propertySetter) {
        this.propertySetter = propertySetter;
    }

    /**
     * Sets the {@link J3DTheme} enum value associated with this locator.
     *
     * @param themeProperty The new theme property.
     */
    public void setThemeProperty(J3DTheme themeProperty) {
        this.themeProperty = themeProperty;
    }
}
