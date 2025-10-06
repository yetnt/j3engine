package com.j3d.engine.interact.cmd.base;

/**
 * Subcommand class that extends Command and implements Argument.
 * This class can be used to create subcommands within a command structure.
 */
public class Subcommand extends Command implements Argument {
    public Subcommand(String name, String description) {
        super(name, description);
    }

    @Override
    public String getName() {
        return aliases.getFirst();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return false;
    }
}
