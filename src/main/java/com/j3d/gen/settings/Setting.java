package com.j3d.gen.settings;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.spec.SettingUpdatedPayload;
import com.j3d.ui.settings.panels.AbstractPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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

    public String valueToString(T value) {
        return value.toString();
    }
    public T fromString(String str) {
        return (T) str;
    }

    @Override
    public ArrayList<String> serialize() {
        return new ArrayList<>(
                List.of(
                        serializedName() + " = " + valueToString(value)
                )
        );
    }

    @Override
    public void deserialize(ArrayList<String> leftover) {
        // find the string which contains this thing as a label
        String match =
                leftover.stream()
                        .filter(s -> s.contains(serializedName()))
                        .findFirst()
                        .orElse(null);
        if (match == null) {
            setValueNoBroadcast(defaultValue);
            return;
        }
        String[] parts = match.split("=");
        if (parts.length != 2) {
            setValueNoBroadcast(defaultValue);
            return;
        }
        String strValue = parts[1].trim();
        setValue(fromString(strValue));
        ((AbstractPanel<?, T>)panel()).calculate();
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
        StaticRefs.getMainPanel().repaint();
    }

    public T setValueNoBroadcast(T value) {
        Settings.setPreferencesSaved(false);
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

    public <R extends Component> R panel() {
        return null; // Classes should implement.
    }

    @Override
    public void attachListener(EventListener event) {
        genericAttach(registered, event);
    }

    @Override
    public void detachListener(EventListener event) {
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
