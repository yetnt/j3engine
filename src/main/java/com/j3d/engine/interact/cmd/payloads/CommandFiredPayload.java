package com.j3d.engine.interact.cmd.payloads;

import com.j3d.engine.interact.cmd.Commands;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.react.events.EventPayload;

import java.util.ArrayList;

public class CommandFiredPayload extends EventPayload<Commands> {
    private final Command command;
    private final Invoker invoker;
    private final String aliasUsed;
    private final Object[] argsCopy;
    private final ArrayList<TaggedArgValue<?>> taggedArgsCopy;

    public CommandFiredPayload(Command cmd, Invoker i, String alias, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super(CommandsManager.commands);
        command = cmd;
        invoker = i;
        aliasUsed = alias;
        argsCopy = args.clone();
        taggedArgsCopy = (ArrayList<TaggedArgValue<?>>) taggedArgs.clone();
    }

    public Command getCommand() {
        return command;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public ArrayList<TaggedArgValue<?>> getTaggedArgsCopy() {
        return taggedArgsCopy;
    }

    public Object[] getArgsCopy() {
        return argsCopy;
    }

    public String getAliasUsed() {
        return aliasUsed;
    }
}
