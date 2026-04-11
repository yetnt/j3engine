package com.j3d.engine.interact.cmd.base;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a command argument that is expected to be of a specific type or one of several allowed types.
 * <p>
 * This class is used to define arguments that require type validation. For example, an argument
 * might be required to be a {@code Double}, a {@code String}, or a specific graphics object like a {@code GPoint}.
 * This information is crucial for both parsing user input and for auto-generating accurate command usage strings.
 * </p>
 * @see Argument
 * @see Command#parseUsages()
 */
public class TypedArg implements Argument {
    /**
     * A list of {@link Class} objects representing the allowed types for this argument.
     */
    private final ArrayList<Class> type = new ArrayList<>();
    private final String name;
    private final String description;
    private final boolean optional;

    /**
     * Constructs a new TypedArg.
     *
     * @param name        The name of the argument.
     * @param description A user-friendly description of the argument's purpose.
     * @param optional    {@code true} if the argument is optional, {@code false} if it is required.
     * @param type        A varargs array of {@link Class} objects representing the allowed types.
     */
    public TypedArg(String name, String description, boolean optional, Class<?>... type) {
        this.name = name;
        this.type.addAll(Arrays.asList(type));
        this.description = description;
        this.optional = optional;
    }

    /**
     * Returns the list of allowed types for this argument.
     *
     * @return An {@link ArrayList} of {@link Class} objects.
     */
    public ArrayList<Class> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
