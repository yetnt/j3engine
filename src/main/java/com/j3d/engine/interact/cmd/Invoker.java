package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;

/**
 * Represents the source or context from which a command was invoked.
 * <p>
 *     A command can be invoked by their following classifications:
 *     <ul>
 *         <li>
 *             {@code User}: The user explicitly typed this command out in the command line (or it has been pasted) and they hit
 *             enter in the command lin and it executed. Can only set {@code CommandParser#run()}
 *         </li>
 *         <li>
 *             {@code Engine}: Anything else the user does, but does not have to go through the command line text input.
 *             So clicking a button or a keyboard shortcut, all count as engine invocations.
 *             set within {@link CommandParser#run(Command, ArrayList, ArrayList)} overload
 *         </li>
 *         <li>
 *             {@code fromParent}: This specific command was invoked as a result of a parent command calling it. This specifically
 *             indicates that this was invoked as a Subcommand of the stored parent. This is set only by
 *             {@code Command#dispatchToSubcommands(String, SafeJLabel, Object[], ArrayList)}
 *         </li>
 *         <li>
 *             {@code fromCall}: If another command call specifically calls some other command. The command has to use the
 *             {@link CommandParser#run(Command, ArrayList, ArrayList, Command)} overload.
 *         </li>
 *     </ul>
 * </p>
 * @see CommandParser
 * @see Command
 * @author Lehlogonolo Poole
 */
public class Invoker {
    /**
     * Indicates if the invocation originated from a user action.
     */
    private final boolean user;
    /**
     * Indicates if the invocation originated from the engine itself.
     */
    private final boolean engine;
    /**
     * The parent command that invoked this command as a subcommand of itself, if applicable.
     */
    private final Command invokedFromParent;
    /**
     * The command that called this command if this invoker represents a direct command call.
     */
    private final Command invokedFromCall;

    private Invoker(boolean user, boolean engine, Command parent, Command call) {
        this.user = user;
        this.engine = engine;
        this.invokedFromParent = parent;
        this.invokedFromCall = call;
    }

    /**
     * Creates an Invoker instance indicating the invocation originated from the command line input (the user hit enter)
     *
     * @return An Invoker instance representing a user invocation.
     */
    public static Invoker byUser() {
        return new Invoker(true, false, null, null);
    }

    /**
     * Creates an Invoker instance indicating the invocation originated from the engine. (anything else)
     *
     * @return An Invoker instance representing an engine invocation.
     */
    public static Invoker byEngine() {
        return new Invoker(false, true, null, null);
    }

    /**
     * Creates an Invoker instance indicating the command was invoked as a subcommand
     * by another parent command.
     * @param command The parent command.
     * @return An Invoker instance.
     */
    public static Invoker byParentCommand(Command command) {
        return new Invoker(false, false, command, null);
    }

    /**
     * Creates an Invoker instance indicating the command was invoked directly
     * through a command different call.
     * @param command The command that was called.
     * @return An Invoker instance.
     */
    public static Invoker byCommandCall(Command command) {
        return new Invoker(false, false, null, command);
    }

    /**
     * Checks if the invocation originated from a user action.
     * @return True if invoked by user, false otherwise.
     */
    public boolean isUser() {
        return user;
    }

    /**
     * Checks if the invocation originated from the engine itself.
     * @return True if invoked by engine, false otherwise.
     */
    public boolean isEngine() {
        return engine;
    }

    /**
     * Returns the parent command that invoked this command, if applicable.
     * @return The parent Command, or null if not invoked as a subcommand.
     */
    public Command getInvokedFromParent() {
        return invokedFromParent;
    }

    /**
     * Returns the command that was called, if this invoker represents a direct command call.
     * @return The Command that was called, or null if not a direct call.
     */
    public Command getInvokedFromCall() {
        return invokedFromCall;
    }

    /**
     * Returns a descriptive string indicating the source of the invocation.
     * This will be "USER", "ENGINE", or the simple class name of the invoking command
     * with an indication of whether it was a subcommand or a direct call.
     * @return A string representing the invoker.
     */
    public String getString() {
        if (user || engine) {
            return user ? "USER" : "ENGINE";
        }

        return (invokedFromParent == null
                ? invokedFromCall.getClass().getSimpleName() + " [From call site]"
                : invokedFromParent.getClass().getSimpleName() + " [As a subcommand]");
    }
}
