package com.j3d.settings.types;

import com.j3d.settings.Setting;
import com.j3d.ui.settings.panels.NumberValueSPanel;

import java.awt.*;

public class IntSetting extends Setting<Integer> {
    private final int min;
    private final int max;

    public IntSetting(String name, Integer value, String description, int min, int max) {
        super(name, value, description);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(
                Math.max(min, Math.min(max, value))
        );
    }

    @Override
    public Component panel() {
        return new NumberValueSPanel<>(this, getDefaultValue(), min, max, 1, (Integer i) -> i, (Integer i) -> i);
    }
}
