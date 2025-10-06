package com.j3d.engine.interact.cmd.base;

import java.util.ArrayList;

/**
 * An argument that can take a set of predefined string values.
 * Useful for commands that have options or modes.
 */
public class ArgSet implements Argument {
    private final ArrayList<String> allowedValues = new ArrayList<>();
    private final String name;
    private final String description;
    private final boolean isOptional;

    public ArgSet(String name, String description, boolean isOptional, String... allowedValues) {
        this.name = name;
        this.description = description;
        this.isOptional = isOptional;
        for (String val : allowedValues) {
            this.allowedValues.add(val.toLowerCase());
        }
    }

    public ArrayList<String> getAllowedValues() {
        return allowedValues;
    }

    public boolean isValid(String value) {
        return allowedValues.contains(value.toLowerCase());
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
        return isOptional;
    }
}
