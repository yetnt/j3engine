package com.j3d.engine.interact.cmd.args;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.engine.CommandPallete;

import java.util.ArrayList;

/**
 * A special type of string argument that can take a set of predefined string values.
 * Useful for commands that have options or modes.
 * <p>
 *     ArgSets typically are in the form of {@code [value1|value2|value3]} in string form,
 *     however that form may also be used to define subcommands who take in the exact same
 *     amount and type of parameters.
 * </p>
 * @author Lehlogonolo Poole
 * @see Argument
 * @see Command
 * @see TypedArg
 * @see CommandPallete
 * @see CommandParser
 */
public class ArgSet implements Argument {
    private final ArrayList<String> allowedValues = new ArrayList<>();
    private final String name;
    private final String description;
    private final boolean isOptional;

    /**
     * Constructor for arg set
     * @param name The name of the argument
     * @param description The description of the argument
     * @param isOptional Whether the argument is optional
     * @param allowedValues The allowed values for the argument
     */
    public ArgSet(String name, String description, boolean isOptional, String... allowedValues) {
        this.name = name;
        this.description = description;
        this.isOptional = isOptional;
        for (String val : allowedValues) {
            this.allowedValues.add(val.toLowerCase());
        }
    }

    /**
     * Retrieves the values that this arg set allows.
     * @return The allowed values for the argument
     */
    public ArrayList<String> getAllowedValues() {
        return allowedValues;
    }

    /**
     * Checks whether a given value is valid for this arg set.
     * @param value The value to check
     * @return True if the value is valid, false otherwise
     */
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

    public String toUseString() {
        return "[" + String.join("|", allowedValues) + (isOptional ? "?" : "") + "]";
    }
}
