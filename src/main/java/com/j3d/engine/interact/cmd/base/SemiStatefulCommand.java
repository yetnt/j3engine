package com.j3d.engine.interact.cmd.base;

import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;

import java.awt.event.KeyEvent;

/**
 * A marker interface for any command that may be stateful at some point with it's own specific logic or not.
 * <p>
 *     An example would be {@link TransformCmd} which only makes use of it being stateful such that
 *     other commands do not intercept with the setup before executing subcommands. Immediately after
 *     it releases itself for actual stateful commands to execute.
 * </p>
 * @implSpec it is to the interest of the class to do it's own managing of statefulness and otherwise
 * logging it to the {@link CommandsManager} such that other commands do not interfere. A command who
 * simply requires the {@link KeyEvent#VK_ENTER} and {@link KeyEvent#VK_ESCAPE}, should itself just be
 * a {@link StatefulCommand}
 * @see StatefulCommand
 * @see CommandsManager
 * @see TransformCmd
 * @author Lehlogonolo Poole
 */
public interface SemiStatefulCommand {
}
