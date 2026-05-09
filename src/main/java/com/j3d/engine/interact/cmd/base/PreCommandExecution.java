package com.j3d.engine.interact.cmd.base;

import com.j3d.engine.interact.cmd.base.conditions.SelectionPreCondition;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventReactor;
import com.j3d.ui.util.SafeJLabel;

import java.util.function.Supplier;

/**
 * A basic interface for providing safe guards before a command is executed. This is the bare bones
 * implementation of simply checking if it's true and attaching listeners. Concrete implementations
 * need to make their own clean-up or otherwise logging logic.
 * <p>
 *     An example would be {@link SelectionPreCondition}, where it allows commands to require that a
 *     selection is made before the command can execute. And if on run a selection was not made, it
 *     queries the user for a selection.
 * </p>
 * @implNote Any command which may want to use some system implementing this interface would also
 * need to implement {@link SemiStatefulCommand} (or the more concrete {@link StatefulCommand})
 * such that no other command interferes with the event logic to run this command.
 * @see SemiStatefulCommand
 * @see SelectionPreCondition
 * @see StatefulCommand
 * @author Lehlogonolo Poole
 */
public interface PreCommandExecution {
    /**
     * The main event reactor to run if the condition is met on startup or is later called by the
     * {@link #getEventEmitterToAttachTo()}.
     * @return The event reactor to run.
     */
    EventReactor getPassListener();

    /**
     * The event to fire if the condition is false.
     * @implSpec {@link PreCommandExecution} does not execute this event or attach this event.
     * It's expected to run as a result of {@link #getPassListener()} failing or otherwise custom
     * logic that implementors need to implement.
     * @return The event reactor to run.
     */
    EventReactor getFailListener();

    /**
     * The {@link EventEmitterInterface} which the {@link #getPassListener()} will attach to.
     * @return The event emitter to attach to.
     */
    EventEmitterInterface getEventEmitterToAttachTo();

    /**
     * The text to display to the user via {@link SafeJLabel#setText(String)} when the condition is
     * not met on startup. This is usually a friendly message requesting the user to interact with
     * something before proceeding.
     * @return The text to display.
     */
    String getLogText();

    /**
     * The actual condition to check against wrapped around a {@link Supplier}
     * @return The condition to check against.
     */
    Supplier<Boolean> getCondition();

    /**
     * Executes the pre-command condition check.
     * If the condition is not met, it displays a message to the user and attaches the pass listener
     * to the appropriate event emitter, waiting for the condition to be met.
     * If the condition is met, it immediately triggers the pass listener.
     * @param logLabel The {@link SafeJLabel} to display messages to the user.
     * @return {@code true} if the condition was met immediately, {@code false} otherwise.
     */
    default boolean execute(SafeJLabel logLabel) {
        if (!getCondition().get()) {
            logLabel.setText(getLogText());
            if (!getEventEmitterToAttachTo().isAttached(getPassListener()))
                getEventEmitterToAttachTo().attach(getPassListener());
            return false;
        }
        else {
            getPassListener().onEvent(null, null);
            return true;
        }
    }
}
