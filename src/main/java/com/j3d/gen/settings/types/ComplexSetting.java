package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.PopoutSPanel;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ComplexSetting<T> extends Setting<T> {

    private final Consumer<ActionEvent> onOpen;

    public ComplexSetting(String name, T value, String description, Consumer<ActionEvent> onOpen) {
        super(name, value, description);
        this.onOpen = onOpen;
    }

    @Override
    public PopoutSPanel panel() {
        return new PopoutSPanel(this, onOpen);
    }
}
