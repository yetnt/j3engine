package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.base.Command;

public class Invoker {
    private boolean user = false;
    private boolean engine = false;
    private Command invokedFromParent = null;

    private Invoker(boolean user, boolean engine, Command parent) {
        this.user = user;
        this.engine = engine;
        this.invokedFromParent = parent;
    }

    public static Invoker byUser() {
        return new Invoker(true, false, null);
    }

    public static Invoker byEngine() {
        return new Invoker(false, true, null);
    }

    public static Invoker byParentCommand(Command command) {
        return new Invoker(false, false, command);
    }

    public boolean isUser() {
        return user;
    }

    public boolean isEngine() {
        return engine;
    }

    public Command getInvokedFromParent() {
        return invokedFromParent;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getString() {
        if (user || engine) {
            return user ? "USER" : "ENGINE";
        }

        return invokedFromParent.getClass().getSimpleName();
    }
}
