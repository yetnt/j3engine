package com.j3d.engine.interact.cmd.base.conditions;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.PreCommandExecution;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.react.events.*;

import java.util.function.Supplier;

public class SelectionPreCondition implements PreCommandExecution {

    private final EventReactor passListener;
    private final EventReactor failListener;

    public SelectionPreCondition(Runnable success) {
        passListener = new EventReactor() {
            @Override
            public <K> void onEvent(EventType event, EventPayload<K> properties) {
                if (getCondition().get()) {
                    success.run();
                } else failListener.onEvent(event, properties);
            }
        };
        failListener = new EventReactor() {
            @Override
            public <K> void onEvent(EventType event, EventPayload<K> properties) {
                // do nothing on fail.
            }
        };
    }

    @Override
    public EventReactor getPassListener() {
        return passListener;
    }

    @Override
    public EventReactor getFailListener() {
        return failListener;
    }

    @Override
    public EventEmitterInterface getEventEmitterToAttachTo() {
        return SelectionManager.selectionMouseOwner;
    }

    @Override
    public String getLogText() {
        return "Make a selection then left click to continue this command.";
    }

    @Override
    public Supplier<Boolean> getCondition() {
        return new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !Static.sceneManager.getSelected().isEmpty();
            }
        };
    }
}
