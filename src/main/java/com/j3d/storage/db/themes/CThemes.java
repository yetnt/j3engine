package com.j3d.storage.db.themes;

import com.j3d.storage.db.api.TableColumns;

public enum CThemes implements TableColumns {
    IDENTIFIER("themeId"),
    THEME_NAME("themeName"),
    TEXT_PRIMARY("textPrimary"),
    TEXT_SECONDARY("textSecondary"),
    ACCENT_PRIMARY("accentPrimary"),
    ACCENT_SECONDARY("accentSecondary"),
    UI_SURFACE("uiSurface"),
    BACKGROUND("background");

    private final String value;

    CThemes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
