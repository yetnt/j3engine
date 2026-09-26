package com.j3d.ui.theme.updator;

import com.j3d.ui.theme.DefaultThemes;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.theme.ThemeEntry;
import com.j3d.ui.theme.ThemeKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ThemeUpdaterTest {

    @Test
    void update() {
        ThemeUpdater updater = new ThemeUpdater();
        AtomicBoolean called = new AtomicBoolean(false);
        Consumer<Color> l = (c) -> {
            called.set(true);
            Assertions.assertNotNull(c, "ThemeChanger passed a null value into the colour consumer.");
            Assertions.assertEquals(DefaultThemes.ESPRESSO.getThemeEntry().getEntries().get(ThemeKey.TEXT_PRIMARY), c);
        };

        updater.add(J3DTheme.TEXT_PRIMARY, l);

        J3DTheme.loadTheme(DefaultThemes.ESPRESSO.getThemeEntry());

        updater.update();

        Assertions.assertTrue(called.get(), "Theme was not updated");
    }
}