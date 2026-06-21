package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.PopoutSPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ComplexSetting<T> extends Setting<T> {

    private final BiFunction<ActionEvent, JLabel, T> onOpen;
    private Supplier<String> onCreate;

    public ComplexSetting(String name, T value, String description, BiFunction<ActionEvent, JLabel, T> onOpen, Supplier<String> onCreate) {
        super(name, value, description);
        this.onOpen = onOpen;
        this.onCreate = onCreate;
    }

    @Override
    public PopoutSPanel<T> panel() {
        return new PopoutSPanel<>(onCreate.get(), this, onOpen);
    }
}
