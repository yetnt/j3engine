package com.j3d.settings.types;

import com.j3d.settings.Setting;
import com.j3d.ui.settings.panels.ButtonGroupSPanel;


public class EnumSetting<T extends Enum> extends Setting<T> {
    private final T[] values;

    public EnumSetting(String name, T value, String description, T[] values) {
        super(name, value, description);
        this.values = values;
    }

    public T[] getValues() {
        return values;
    }

    @Override
    public ButtonGroupSPanel panel() {
        return new ButtonGroupSPanel(this);
    }
}
