package com.j3d.gen.settings;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.spec.SettingUpdatedPayload;
import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.ui.settings.panels.AbstractPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.j3d.engine.react.events.EventEmitter.*;

/**
 * A generic class representing a single setting.
 * <p>
 *      This class is used to store a single setting, along with its description. It can be used for any type of setting,
 *      by specifying the type parameter {@code <T>}.
 * </p>
 * <p>
 *     A setting also serializes into a key value pair. (although this is usually with dot syntax)
 *     So the setting {@link CameraProperties#movementSpeed} serializes into:
 *     <pre>{@code
 *     settings.cameraProperties.movementSpeed = 5.0
 *     }</pre>
 *     and alot more logic that's readable in {@link #serialize()} {@link #deserialize(ArrayList)}
 *     provided by {@link SettingsChild} and also found in {@link SettingsParent}
 * </p>
 * @param <T> The type of the setting.
 * @see Settings
 * @see SettingsChild
 * @see SettingsParent
 * @see EventEmitterInterface
 * @author Lehlogonolo Poole
 */
public class Setting<T> implements SettingsChild, EventEmitterInterface {
    private T value;
    private final String description;
    private final T defaultValue;
    private final String name;
    private ArrayList<EventListener> registered = new ArrayList<>();
    private Function<T, Void> callback;

    /**
     * Constructs a new Setting with the given name, initial value, and description.
     * The initial value also serves as the default value.
     * @param name The name of the setting.
     * @param value The initial value of the setting.
     * @param description A brief description of what the setting controls.
     */
    public Setting(String name, T value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.defaultValue = value;
    }

    /**
     * Converts the setting's value to its string representation.
     * @implSpec This method is measnt to be overridden by subclasses.
     * @param value The value to convert to a string.
     * @return The string representation of the value.
     */
    public String valueToString(T value) {
        return value.toString();
    }

    /**
     * Converts a string representation back to the setting's type.
     * @implSpec This method is meant to be overridden by subclasses
     * @param str The string to convert.
     * @return The value of type T parsed from the string.
     * @throws ClassCastException if the string cannot be cast to type T (for the default implementation).
     */
    public T fromString(String str) {
        return (T) str;
    }

    /**
     * Serializes the current setting into a list of strings.
     * For a single setting, this typically returns a list containing one string
     * in the format "settingName = settingValue".
     * @return An {@link ArrayList} of strings representing the serialized setting.
     */
    @Override
    public ArrayList<String> serialize() {
        return new ArrayList<>(
                List.of(
                        serializedName() + " = " + valueToString(value)
                )
        );
    }

    /**
     * Deserializes the setting's value from a list of strings.
     * It searches for a string containing the serialized name of this setting,
     * parses its value, and updates the setting. If no match is found or parsing fails,
     * the setting is reset to its default value.
     * @param leftover An {@link ArrayList} of strings from which to deserialize the setting.
     */
    @Override
    public void deserialize(ArrayList<String> leftover) {
        // finder the string which contains this thing as a label
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

    /**
     * Returns the name of this setting.
     * @return The name of the setting.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current value of this setting.
     * If the current value is null, the default value is returned.
     * @return The current value of the setting.
     */
    public T getValue() {
        return value == null ? defaultValue : value;
    }

    /**
     * Sets the value of this setting and broadcasts an update event.
     * @implNote If {@link EventType#SETTING_CODE_UPDATED} event needs to be avoided for some
     * particular reason then rather use {@link #setValueNoBroadcast(Object)}
     * @param value The new value to set.
     */
    public void setValue(T value) {
        T old = setValueNoBroadcast(value);
        broadcast(EventType.SETTING_CODE_UPDATED,new SettingUpdatedPayload<T>(
                this, old, value
        ));
        StaticRefs.getMainPanel().repaint();
    }

    /**
     * Sets the value of this setting without broadcasting a specific {@code SETTING_CODE_UPDATED} event.
     * @implSpec It still broadcasts a generic {@code SUPDATED} event and marks preferences as unsaved.
     * If a callback is registered, it will be invoked.
     * @implNote This is the actual "set" call.
     * @param value The new value to set.
     * @return The old value of the setting before the update.
     */
    public T setValueNoBroadcast(T value) {
        Settings.setPreferencesSaved(false);
        broadcast(EventType.SUPDATED, new EventPayload<Object>(this) {});
        if (callback != null) callback.apply(value);
        T old = this.value;
        this.value = value;
        return old;
    }

    /**
     * Returns the default value of this setting.
     * @return The default value.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the description of this setting.
     * @return The description string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the panel associated with this setting for display and interaction.
     * @implSpec Subclasses should override this method to provide a concrete panel implementation.
     * @param <R> The type of the UI component, which must extend {@link Component}.
     * @return A UI component representing this setting, or {@code null} if not implemented by a subclass.
     */
    public <R extends Component> R panel() {
        return null; // Classes should implement.
    }

    /**
     * Attaches an {@link EventListener} to this setting to receive updates.
     * @param event The listener to attach.
     */
    @Override
    public void attachListener(EventListener event) {
        genericAttach(registered, event);
    }

    /**
     * Detaches an {@link EventListener} from this setting.
     * @param event The listener to detach.
     */
    @Override
    public void detachListener(EventListener event) {
        genericDetach(registered, event);
    }

    /**
     * Detaches all registered {@link EventListener}s from this setting.
     */
    @Override
    public void detachAll() {
        genericDetachAll(registered);
    }

    /**
     * Broadcasts an event of a specific type with associated properties to all registered listeners.
     * @param <K> The emitter of the payload.
     * @param eventType The type of the event to broadcast.
     * @param properties The payload containing event-specific data.
     */
    @Override
    public <K> void broadcast(EventType eventType, EventPayload<K> properties) {
        genericBroadcast(registered, eventType, properties);
    }

    /**
     * Checks if a specific {@link EventListener} is currently attached to this setting.
     * @param e The listener to check.
     * @return {@code true} if the listener is attached, {@code false} otherwise.
     */
    @Override
    public boolean isAttached(EventListener e) {
        return registered.contains(e);
    }

    /**
     * Registers a callback function to be executed whenever the setting's value is changed.
     * The callback receives the new value as an argument.
     * @param callback The {@link Function} to be called on value change.
     * @return This {@link Setting} instance, allowing for method chaining.
     */
    public Setting<T> onSetValue(Function<T, Void> callback) {
        this.callback = callback;
        return this;
    }
}
