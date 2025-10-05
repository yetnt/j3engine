package com.j3d.engine.interact.cmd.base;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Command {
    public ArrayList<String> aliases = new ArrayList<>();
    public String description;
    public String usage = "This will be auto-generated.";
    public ArrayList<Argument> args = new ArrayList<>();

    public Command(String name, String d) {
        aliases.add(name);
        description = d;
    }

    public Command aliases(String... a) {
        aliases.addAll(Arrays.asList(a));
        return this;
    }

    public Command args(Argument... a) {
        args.addAll(Arrays.asList(a));
        return this;
    }

    /**
     * The method to be overridden by subclasses to implement command functionality.
     * @param args The arguments passed to the command.
     */
    public void run(JLabel logLabel, Object... args) {
        // To be overridden by subclasses
    }

}
