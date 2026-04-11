package com.j3d.engine.interact.cmd.base;

/**
 * Defines the contract for a command argument in the command processing system.
 * <p>
 * An Argument represents a parameter that can be passed to a command, complete with
 * its name, a description for help menus, and whether it is required or optional.
 * This interface is the base for more specific argument types, such as {@link ArgSet}.
 *
 * @see Command
 * @see ArgSet
 * @see Subcommand
 * @see TaggedArgUtil
 * @see TypedArg
 */
public interface Argument {
    /**
     * Returns the name of the argument.
     * <p>
     * This is the identifier used by the command parser to refer to this argument.
     *
     * @return The name of the argument.
     */
    String getName();

    /**
     * Provides a user-friendly description of what the argument does.
     * <p>
     * This is typically used for generating help text or tooltips.
     *
     * @return A brief description of the argument's purpose.
     */
    String getDescription();

    /**
     * Specifies whether the argument is optional or required for the command to execute.
     *
     * @return {@code true} if the argument is optional, {@code false} if it is required.
     */
    boolean isOptional();
}
