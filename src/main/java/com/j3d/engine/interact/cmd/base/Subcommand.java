package com.j3d.engine.interact.cmd.base;

/**
 * Subcommand is a command that is a subcommand of another command.
 * It inherits from Command and does not add any new functionality.
 * It is used to differentiate between commands and subcommands.
 */
public class Subcommand extends Command implements Argument {
    public Subcommand(String name, String description) {
        super(name, description);
    }
}
