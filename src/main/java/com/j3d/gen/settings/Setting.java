package com.j3d.gen.settings;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.spec.SettingUpdatedPayload;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;

import static com.j3d.engine.react.events.EventEmitter.*;

/**
 * A generic class representing a single setting.
 * <p>
 * This class is used to store a single setting, along with its description. It can be used for any type of setting,
 * by specifying the type parameter {@code <T>}.
 * </p>
 * @param <T> The type of the setting.
 */
public class Setting<T> implements SettingsChild, EventEmitterInterface {
    private T value;
    private final String description;
    private final T defaultValue;
    private final String name;
    private ArrayList<EventListener> registered = new ArrayList<>();
    private Function<T, Void> callback;

    public Setting(String name, T value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.defaultValue = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value == null ? defaultValue : value;
    }

    public void setValue(T value) {
        T old = setValueNoBroadcast(value);
        broadcast(EventType.SETTINGS_CODE_UPDATED,new SettingUpdatedPayload<T>(
                this, old, value
        ));
        StaticRefs.mainPanel.repaint();
    }

    public T setValueNoBroadcast(T value) {
        if (callback != null) callback.apply(value);
        T old = this.value;
        this.value = value;
        return old;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public Component panel() {
        return null; // Classes should implement.
    }

    @Override
    public void attach(EventListener event) {
        genericAttach(registered, event);
    }

    @Override
    public void detach(EventListener event) {
        genericDetach(registered, event);
    }

    @Override
    public void detachAll() {
        genericDetachAll(registered);
    }

    @Override
    public <K> void broadcast(EventType eventType, EventPayload<K> properties) {
        genericBroadcast(registered, eventType, properties);
    }

    @Override
    public boolean isAttached(EventListener e) {
        return registered.contains(e);
    }


    public Setting onSetValue(Function<T, Void> callback) {
        this.callback = callback;
        return this;
    }
}
