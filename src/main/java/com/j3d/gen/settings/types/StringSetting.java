package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.EnterValueSPanel;

import java.awt.*;
import java.util.function.Function;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String value, String description) {
        super(name, value, description);
    }

    @Override
    public EnterValueSPanel panel() {
        return new EnterValueSPanel(this, getDefaultValue());
    }

    @Override
    public StringSetting onSetValue(Function<String, Void> callback) {
        return (StringSetting) super.onSetValue(callback);
    }

    @Override
    public String fromString(String str) {
        return str;
    }
}
