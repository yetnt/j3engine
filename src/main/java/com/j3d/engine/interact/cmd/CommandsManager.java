package com.j3d.engine.interact.cmd;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.commands.*;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.orbit.OrbitCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.ui.engine.CommandPallete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CommandsManager is responsible for creating and storing available command instances
 * and providing lookup utilities for retrieving commands by their aliases.
 *
 * <p>It also manages a single global reference to the currently running {@link SemiStatefulCommand}
 * (if any). This class mixes two responsibilities:
 * - holding the registry of available commands (instance field {@link #commands})
 * - tracking the currently active stateful command (static {@link #currentStatefulCommand}).
 *
 * @author Lehlogonolo Poole
 * @see StatefulCommand
 * @see CommandParser
 * @see CommandPallete
 */
public class CommandsManager {

    /**
     * The currently active stateful command, if any.
     *
     * <p>Only one {@link StatefulCommand} can be active globally at a time. This field is static
     * so that any part of the codebase can check or clear the current command via the static
     * helper methods in this class.
     *
     * <p>Possible values:
     * - null: no stateful command is currently running
     * - non-null: reference to the active {@link StatefulCommand}
     */
    public static SemiStatefulCommand currentStatefulCommand = null;

    /**
     * Mark the supplied {@code StatefulCommand} as the currently active one.
     *
     * @param c the {@link StatefulCommand} to set as current; may be null to clear
     */
    public static void setAsCurrent(SemiStatefulCommand c) {
        currentStatefulCommand = c;
    }

    /**
     * Check whether the given {@code StatefulCommand} instance is the one currently marked as running.
     *
     * @param c the {@link StatefulCommand} instance to check
     * @return {@code true} if {@code currentStatefulCommand} == {@code c}, {@code false} otherwise
     */
    public static boolean isCurrentStatefulRunning(SemiStatefulCommand c) {
        return currentStatefulCommand == c;
    }

    /**
     * Clear the currently active stateful command (set to {@code null}).
     */
    public static void clearCurrent() {
        currentStatefulCommand = null;
    }

    /**
     * Registry of available commands mapped by their alias strings.
     *
     * <p>Key: a single alias string (case-sensitive) that can be used to look up a command.
     * Value: the {@link Command} instance associated with that alias.
     *
     * <p>The map is populated in the constructor: each concrete command's {@code aliasStream()}
     * is iterated and each alias is registered pointing to the same command instance. This
     * allows fast O(1) alias lookups via {@link #getCommand(String)}.
     */
    public HashMap<String, Command> commands = new HashMap<>();

    /**
     * Constructs a CommandsManager and populates the {@link #commands} map with the known command instances.
     *
     * @implSpec Because concrete command instances are constructed here, creating a new {@code CommandsManager}
     * constructs all commands. Avoid creating many ephemeral {@code CommandsManager} instances if
     * command construction is expensive.
     */
    public CommandsManager() {
        DebugCmd debugCmd = new DebugCmd();
        TransformCmd transformCmd = new TransformCmd();
        LookAtCmd lookAtCmd = new LookAtCmd();
        TeleportCmd tpCmd = new TeleportCmd();
        OrbitCmd  orbitCmd = new OrbitCmd();
        EngineCmd engineCmd = new EngineCmd();

        ArrayList<Command> commands = new ArrayList<>(
                List.of(
                        debugCmd, transformCmd, lookAtCmd,
                        tpCmd, orbitCmd, engineCmd
                )
        );

        commands.forEach(
                c ->
                    c.aliasStream().forEach(
                            a -> this.commands.put(a, c)
                    )
        );
    }

    /**
     * Retrieve a {@link Command} by an alias name.
     *
     * @param name alias to look up (case-sensitive)
     * @return the {@link Command} registered for {@code name}, or {@code null} if none found
     * @see #commands
     */
    public static Command getCommand(String name) {
        return Static.commandManager.commands.getOrDefault(name, null);
    }

    /**
     * Query whether a stateful command is currently running.
     *
     * @return {@code true} if {@link #currentStatefulCommand} is non-null, {@code false} otherwise
     */
    public static boolean commandIsRunning() {
        return currentStatefulCommand != null;
    }

    public static String getCurrentCommandName() {
        return ((Command)currentStatefulCommand).aliases.getFirst();
    }
}