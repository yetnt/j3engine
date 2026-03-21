package com.j3d.settings.types;

import com.j3d.settings.Setting;
import com.j3d.ui.settings.panels.EnterValueSPanel;

import java.awt.*;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String value, String description) {
        super(name, value, description);
    }

    @Override
    public Component panel() {
        return new EnterValueSPanel(this, getDefaultValue());
    }
}
