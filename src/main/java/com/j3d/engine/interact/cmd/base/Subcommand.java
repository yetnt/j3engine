package com.j3d.engine.interact.cmd.base;

/**
 * Represents a command that is nested within another command, acting as both a
 * {@link Command} in its own right and as an {@link Argument} to its parent command.
 * <p>
 * This class enables the creation of hierarchical command structures, such as
 * {@code git commit -m "message"}. In this example, {@code commit} would be a
 * {@code Subcommand} of the main {@code git} command.
 * <p>
 * By default, a subcommand is considered a non-optional argument to its parent.
 *
 * @author Lehlogonolo Poole
 * @see Command
 * @see Argument
 */
public class Subcommand extends Command implements Argument {

    /**
     * Constructs a new Subcommand.
     *
     * @param name        The primary name of the subcommand.
     * @param description A user-friendly description of what the subcommand does.
     */
    public Subcommand(String name, String description) {
        super(name, description);
    }

    /**
     * Returns the primary name of this subcommand.
     *
     * @return The first alias, which is considered the primary name.
     */
    @Override
    public String getName() {
        return aliases.getFirst();
    }

    /**
     * Returns the description of this subcommand.
     *
     * @return The user-friendly description.
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Specifies that this subcommand is a required argument for its parent command.
     *
     * @return always {@code false}, as subcommands are not optional.
     */
    @Override
    public boolean isOptional() {
        return false;
    }
}
