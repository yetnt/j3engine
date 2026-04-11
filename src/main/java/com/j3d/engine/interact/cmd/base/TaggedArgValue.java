package com.j3d.engine.interact.cmd.base;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.ui.util.SafeJLabel;

import java.util.ArrayList;

/**
 * Represents an instance of a parsed tagged argument, holding its name, its value,
 * and its expected type.
 * <p>
 * This class serves as the data container for a single key-value pair identified by the
 * {@link TaggedArgUtil}. An {@link ArrayList} of these objects is passed to a command's
 * {@code run} method for processing.
 *
 * @param <T> The expected type of the argument's value.
 * @author Lehlogonolo Poole
 * @see Argument
 * @see TaggedArgUtil
 * @see CommandParser
 */
public class TaggedArgValue<T> {
    public String taggedArgName;
    public T value;
    public Class<?> type;

    private boolean isEmpty = true;
    private boolean err = false;

    private TaggedArgValue(T v) {
        value = v;
    }

    /**
     * Constructs a shell for a tagged argument, defining its expected type.
     * This is used to populate the {@link TaggedArgUtil#acceptedTags} map.
     *
     * @param type The {@link Class} of the value this tag expects.
     */
    public TaggedArgValue(Class<?> type) {
        this.type = type;
    }

    /**
     * Sets the name for this tagged argument.
     *
     * @param name The tag's name (the "key" in the key-value pair).
     * @return This instance for method chaining.
     */
    public TaggedArgValue<T> setName(String name) {
        isEmpty = false;
        taggedArgName = name;
        return this;
    }

    /**
     * Marks this tagged argument instance as having a parsing error.
     *
     * @return This instance for method chaining.
     */
    public TaggedArgValue<T> error() {
        err = true;
        return this;
    }

    /**
     * Sets the expected type for this tagged argument.
     *
     * @param clazz The {@link Class} of the value.
     * @return This instance for method chaining.
     */
    public TaggedArgValue<T> setType(Class<?> clazz) {
        type = clazz;
        return this;
    }

    /**
     * Creates a new, populated instance of a {@code TaggedArgValue} with a parsed value.
     * <p>
     * This method validates that the provided value's type matches the expected type for this tag.
     *
     * @param val   The parsed value object.
     * @param label A {@link SafeJLabel} for reporting type mismatch errors.
     * @return A new, populated {@code TaggedArgValue} instance, or an error-marked instance if types are incompatible.
     */
    public TaggedArgValue<?> copy(Object val, SafeJLabel label) {
        if (val instanceof Integer i && type == Double.class) {
            val = (double) i;
        }
        if (!type.isInstance(val)) {
            label.setText("Invalid type given to tagged argument: " + val.getClass().getSimpleName().toLowerCase() + ". Requires: " + type.getSimpleName().toLowerCase());
            return new TaggedArgValue<>(null).error();
        }
        return new TaggedArgValue<>(val).setName(taggedArgName).setType(type);
    }

    /**
     * Checks if this instance is an un-parsed shell.
     *
     * @return {@code true} if the instance is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Checks if a parsing error occurred while creating this instance.
     *
     * @return {@code true} if an error was flagged, {@code false} otherwise.
     */
    public boolean isErr() {
        return err;
    }

    @Override
    public String toString() {
        return "{" + taggedArgName + ": " + value.toString() + "}";
    }
}
