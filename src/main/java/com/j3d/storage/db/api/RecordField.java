package com.j3d.storage.db.api;

import java.util.function.Function;

/**
 * A singular field within a record.
 * @param <T> The type of the field.
 */
public class RecordField<T> {
    /**
     * The name of the field.
     */
    public final String name;
    private T value;
    private boolean updated = false;
    private final Function<T, String> converter;

    /**
     * Default Constructor
     * @param name The name of the field.
     * @param value The value of the field.
     */
    public RecordField(String name, T value) {
        this.name = name;
        this.value = value;
        this.converter = null;
    }

    /**
     * Constructor with converter.
     * @param name The name of the field.
     * @param value The value of the field.
     * @param converter The converter function, if this field isn't a simple type.
     */
    public RecordField(String name, T value, Function<T, String> converter) {
        this.name = name;
        this.value = value;
        this.converter = converter;
    }

    /**
     * Gets the value of the field.
     * @return The value of the field.
     */
    public T getValue() {
        return value;
    }

    /**
     * Gets the value of the field in the database format.
     * @return The value of the field in the database format otherwise the normal value if {@link this#converter} is not defined using {@link RecordField#RecordField(String, Object, Function)}
     */
    public Object getDbValue() {
        if (converter != null)
            return converter.apply(value);
        return value;
    }

    /**
     * Checks if the field has been updated. If so, returns true and resets the updated flag. Otherwise, returns false.
     * @return True if the field has been updated, false otherwise.
     */
    public boolean consumeUpdated() {
        if (this.updated) {
            this.updated = false;
            return true;
        }
        return false;
    }

    /**
     * Sets the value of the field.
     * @param value The new value of the field.
     */
    public void setValue(T value) {
        this.value = value;
        this.updated = true;
    }
}
