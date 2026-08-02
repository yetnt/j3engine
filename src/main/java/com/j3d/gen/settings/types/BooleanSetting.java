package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.BooleanValueSPanel;

import java.awt.*;
import java.util.function.Function;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean value, String description) {
        super(name, value, description);
    }

    @Override
    public BooleanValueSPanel panel() {
        return new BooleanValueSPanel(this, getValue());
    }

    @Override
    public BooleanSetting onSetValue(Function<Boolean, Void> callback) {
        return (BooleanSetting) super.onSetValue(callback);
    }

    @Override
    public Boolean fromString(String str) {
        return switch (str.toLowerCase()) {
            case "true", "yes", "yebo" -> true;
            case "false", "no", "aowa" -> false;
            default -> getDefaultValue();
        };
    }
}
