package com.j3d.ui.theme;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ThemeUpdater {

    private ArrayList<Locator> entries = new ArrayList<>();

    public ThemeUpdater() {

    }

    public static final class Locator {
        private J3DTheme themeProperty;
        private Consumer<Color> propertySetter;

        public Locator(
                J3DTheme themeProperty,
                Consumer<Color> propertySetter
        ) {
            this.themeProperty = themeProperty;
            this.propertySetter = propertySetter;
        }

        public J3DTheme themeProperty() {
            return themeProperty;
        }

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

        @Override
        public int hashCode() {
            return Objects.hash(themeProperty, propertySetter);
        }

        @Override
        public String toString() {
            return "Locator[" +
                    "themeProperty=" + themeProperty + ", " +
                    "propertySetter=" + propertySetter + ']';
        }

        public void setPropertySetter(Consumer<Color> propertySetter) {
            this.propertySetter = propertySetter;
        }

        public void setThemeProperty(J3DTheme themeProperty) {
            this.themeProperty = themeProperty;
        }
    }

    public void remove(Locator l) {
        entries.remove(l);
    }

    public Locator add(J3DTheme themeProperty, Consumer<Color> propertySetter) {
        Locator l = new Locator(themeProperty, propertySetter);
        entries.add(l);
        return l;
    }

    public void update() {
        for (Locator l : entries) {
            l.propertySetter().accept(l.themeProperty().color());
        }
    }

}
