package com.j3d.settings.types;

import com.j3d.settings.Setting;
import com.j3d.ui.settings.panels.BooleanValueSPanel;

import java.awt.*;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean value, String description) {
        super(name, value, description);
    }

    @Override
    public Component panel() {
        return new BooleanValueSPanel(this, getValue());
    }
}
