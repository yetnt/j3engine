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
    default boolean execute(SafeJLabel logLabel) {
        if (!getCondition().get()) {
            logLabel.setText(getLogText());
            if (!getEventEmitterToAttachTo().isAttached(getPassListener()))
                getEventEmitterToAttachTo().attach(getPassListener());
            if (!getEventEmitterToAttachTo().isAttached(getFailListener()))
                getEventEmitterToAttachTo().attach(getFailListener());
            return false;
        }
        else {
            getPassListener().onEvent(null, null);
            return true;
        }
    }
}
