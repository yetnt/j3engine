package com.j3d.engine.react.actions;

import java.time.LocalTime;

/**
 * AbstractAction is an abstract class that implements the Action interface with default behaviors.
 */
public abstract class AbstractAction implements VoidAction {

    private final LocalTime now = LocalTime.now();

    @Override
    public Void run() {
        return null;
    }

    @Override
    public void undo() {

    }

    @Override
    public boolean isReversible() {
        return true;
    }

    @Override
    public String getDescription() {
        return "AbstractActionPlaceholder";
    }

    @Override
    public LocalTime getTime() {
        return now;
    }
}
