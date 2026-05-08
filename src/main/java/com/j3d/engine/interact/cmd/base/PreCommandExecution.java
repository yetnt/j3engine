package com.j3d.engine.interact.cmd.base;

import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventReactor;
import com.j3d.ui.util.SafeJLabel;

import java.util.function.Supplier;

public interface PreCommandExecution {
    EventReactor getPassListener();
    EventReactor getFailListener();
    EventEmitterInterface getEventEmitterToAttachTo();
    String getLogText();
    Supplier<Boolean> getCondition();
    default void execute(SafeJLabel logLabel) {
        if (!getCondition().get()) {
            logLabel.setText(getLogText());
            getEventEmitterToAttachTo().attach(getPassListener());
            getEventEmitterToAttachTo().attach(getFailListener());
        }
    }
}
