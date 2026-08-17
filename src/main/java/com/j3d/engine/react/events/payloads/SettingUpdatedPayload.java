package com.j3d.engine.react.events.payloads;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.settings.Setting;

/**
 * Event payload for when a {@link Setting} is updated via code.
 * <p>
 *     This stores the {@code oldValue} and {@code newValue} of the setting.
 * </p>
 * @see EventType#SETTING_CODE_UPDATED
 * @see com.j3d.gen.settings
 * @see Setting
 * @author Lehlogonolo Poole
 * @param <T> The specific type that the {@link Setting} object contains.
 */
public class SettingUpdatedPayload<T> extends EventPayload<Setting<T>> {
    private final T newValue;
    private final T oldValue;

    public SettingUpdatedPayload(Setting<T> e, T old, T newValue) {
        super(e);
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
