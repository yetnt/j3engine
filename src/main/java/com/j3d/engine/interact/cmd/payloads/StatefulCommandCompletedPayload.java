package com.j3d.engine.interact.cmd.payloads;

import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.react.events.EventPayload;

public class StatefulCommandCompletedPayload extends EventPayload<StatefulCommand<?>> {

    private final CompletionType completionType;

    public StatefulCommandCompletedPayload(StatefulCommand<?> e, boolean userHitEnter) {
        super(e);
        completionType = userHitEnter ? CompletionType.ENTER : CompletionType.CANCEL;
    }

    public CompletionType getCompletionType() {
        return completionType;
    }

    public enum CompletionType {
        ENTER, CANCEL
    }
}
