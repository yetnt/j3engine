package com.j3d.ui.settings.panels;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.settings.Setting;
import com.j3d.gen.settings.SettingsChild;

import javax.swing.*;

/**
 * Abstract base class for UI panels that represent and allow modification of a specific {@link Setting}.
 * This class provides a common structure for panels that display and interact with a setting,
 * ensuring that the panel is linked to its corresponding setting object.
 *
 * @param <T> The type of {@link Setting} that this panel represents. It must extend {@link Setting}
 *            and its value type must be {@code R}.
 * @param <R> The type of the value held by the {@link Setting} ({@code T}).
 * @author Lehlogonolo Poole
 */
public class AbstractPanel<T extends Setting<R>, R> extends JPanel implements SettingPanel {

    /**
     * The {@link Setting} object that this panel represents and interacts with.
     * This field is {@code protected} to allow subclasses to access the setting directly,
     * and {@code final} because the setting cannot be changed after panel creation.
     */
    protected final T setting;

    public AbstractPanel(T setting) {
        this.setting = setting;
        setting.attachListener(this);
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {

    }

    @Override
    public void calculate() {

    }
}
