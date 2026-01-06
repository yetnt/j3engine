package com.j3d.engine.react.history;

/**
 * AbstractAction is an abstract class that implements the Action interface with default behaviors.
 */
public abstract class AbstractAction implements Action<Void> {

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
}
