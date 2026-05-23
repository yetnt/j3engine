package com.j3d.engine.interact.cmd.complete;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.base.Command;

import java.util.ArrayList;

public class AutoCompleteSession {
    private String cmdName;
    private Command command;
    private boolean validSession = false;

    private ArrayList<Object> arguments = new ArrayList<>();
    private String currentArg = "";

    public AutoCompleteSession(String cmdName) {
        this.cmdName = cmdName;
        Command c = Static.commandManager.commandsAliasMap.get(cmdName);
        if (c != null) {
            this.command = c;
            this.validSession = true;
        } else
            this.validSession = false;
    }

    public AutoCompleteSession(String subcommandName, Command parentCommand) {
        this.cmdName = subcommandName;
        parentCommand.args.stream().filter(
                cmd ->
                    cmd instanceof Subcommand && ((Subcommand) cmd).aliases.contains(subcommandName)
        ).findAny().ifPresent(
                cmd -> {
                    this.validSession = true;
                    this.command = (Subcommand) cmd;
                }
        );
        if (!this.validSession)
            this.command = null;
    }

    public void addArg(Object arg) {
        this.arguments.add(arg);
        updateSuggestions();
        this.currentArg = "";
    }

    public void setCurrentArg(String newValue) {
        this.currentArg = newValue;
        updateSuggestions();
    }

    public String getCmdName() {
        return cmdName;
    }

    public boolean isValidSession() {
        return validSession;
    }

    public Command getCommand() {
        return command;
    }

    private void updateSuggestions() {
        String[] possibleUsages = command.returnUsagesWhere(
                cmdName,
                arguments.stream()
                        .map(Object::getClass)
                        .toArray(Class[]::new)
        );
        if (possibleUsages.length == 0) {
            Static.hoverLabel.setText("No such command.");
            return;
        }
    }
}
