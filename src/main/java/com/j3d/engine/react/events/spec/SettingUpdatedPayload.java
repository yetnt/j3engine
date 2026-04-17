package com.j3d.engine.react.events.spec;

import com.j3d.Static;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.settings.Setting;

public class SettingUpdatedPayload<T> extends EventPayload<Setting<T>> {
    private final T newValue;
    private final T oldValue;

    public SettingUpdatedPayload(Setting<T> e, T old, T newValue) {
        super(e, Static.sceneManager);
        this.newValue = newValue;
        this.oldValue = old;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
