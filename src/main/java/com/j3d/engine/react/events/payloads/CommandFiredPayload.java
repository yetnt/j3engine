package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.Commands;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

import java.util.ArrayList;

/**
 * Event payload for when any command has been fired.
 * <p>
 *     This event stores:
 *     <ul>
 *         <li> The {@link Command} instance of the command being fired (the emitter of the event) </li>
 *         <li> The {@link Invoker} which describes the invocation of the command </li>
 *         <li> The {@code aliasUsed} to invoke the command </li>
 *         <li> The {@code argsCopy[]} to view the arguments that got passed into the command </li>
 *         <li> The {@code taggedArgsCopy} to view the tagged arguments that got passed into the command </li>
 *     </ul>
 * </p>
 * @implSpec
 * <p>
 *     A command being "fired" does not mean the command actually ran to completion. In some cases it may have
 *     exited early due to some configuration not being available or otherwise it is itself a stateful command
 *     and is technically still running. This event is fired at the very beginning of any command regardless
 *     of whether it may fail or not.
 * </p>
 * <br>
 * <p>
 *     If specifically need to listen for when a {@link StatefulCommand} has completed, listen for
 *     {@link EventType#STATEFUL_COMMAND_COMPLETED} using the payload {@link StatefulCommandCompletedPayload}
 *     instead. However it does not have any of these details other than the command who
 * </p>
 * @see Command
 * @see Commands
 * @see EventType#COMMAND_FIRED
 * @see Invoker
 * @see CommandParser
 * @author Lehlogonolo Poole
 */
public class CommandFiredPayload extends EventPayload<Command> {
    private final Invoker invoker;
    private final String aliasUsed;
    private final Object[] argsCopy;
    private final ArrayList<TaggedArgValue<?>> taggedArgsCopy;

    public CommandFiredPayload(Command cmd, Invoker i, String alias, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super(cmd);
        invoker = i;
        aliasUsed = alias;
        argsCopy = args.clone();
        taggedArgsCopy = (ArrayList<TaggedArgValue<?>>) taggedArgs.clone();
    }

    public Command getCommand() {
        return emitter;
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
