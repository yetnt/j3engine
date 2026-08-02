package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.PopoutSPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ComplexSetting<T> extends Setting<T> {

    private final BiFunction<ActionEvent, JLabel, T> onOpen;
    private Supplier<String> onCreate;
    private boolean isSerializable;
    private Function<T, String> forward = Object::toString;
    private Function<String, T> backward = (String s) -> (T) s;


    public ComplexSetting(String name, T value, String description, BiFunction<ActionEvent, JLabel, T> onOpen, Supplier<String> onCreate) {
        super(name, value, description);
        this.onOpen = onOpen;
        this.onCreate = onCreate;
    }

    public ComplexSetting<T> serializable(Function<T, String> forward, Function<String, T> backward) {
        isSerializable = true;
        this.forward = forward;
        this.backward = backward;
        return this;
    }

    @Override
    public T fromString(String str) {
        return backward.apply(str);
    }

    @Override
    public String valueToString(T value) {
        return forward.apply(value);
    }

    @Override
    public void deserialize(ArrayList<String> leftover) {
        if (isSerializable)
                super.deserialize(leftover);
            // can't really ser
    }

    @Override
    public ArrayList<String> serialize() {
        return
                isSerializable ? super.serialize() :
                new ArrayList<>(); // can't really ser
    }

    @Override
    public PopoutSPanel<T> panel() {
        return new PopoutSPanel<>(onCreate.get(), this, onOpen);
    }
}
