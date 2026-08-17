package com.j3d.engine.react.events.payloads;

import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

/**
 * Event payload for when a {@link StatefulCommand} has completed its execution.
 * <p>
 *     This stores the {@link StatefulCommand} that completed and the {@link CompletionType}
 *     indicating how it completed (e.g., by user pressing Enter or by being cancelled).
 * </p>
 * @see EventType#STATEFUL_COMMAND_COMPLETED
 * @see StatefulCommand
 * @see CompletionType
 * @author Lehlogonolo Poole
 */
public class StatefulCommandCompletedPayload extends EventPayload<StatefulCommand<?>> {

    private final CompletionType completionType;

    public StatefulCommandCompletedPayload(StatefulCommand<?> e, boolean userHitEnter) {
        super(e);
        completionType = userHitEnter ? CompletionType.ENTER : CompletionType.CANCEL;
    }

    public CompletionType getCompletionType() {
        return completionType;
    }

    /**
     * Represents the manner in which a {@link StatefulCommand} completed its execution.
     */
    public enum CompletionType {
        /**
         * Indicates the command completed because the user pressed Enter,
         * signifying a successful or intended completion.
         */
        ENTER,
        /**
         * Indicates the command was cancelled, either by the user or programmatically,
         * before reaching a successful completion state.
         */
        CANCEL
    }
}
