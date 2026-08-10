package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.base.Command;

public class Invoker {
    private boolean user = false;
    private boolean engine = false;
    private Command invokedFromParent = null;
    private Command invokedFromCall = null;

    private Invoker(boolean user, boolean engine, Command parent, Command call) {
        this.user = user;
        this.engine = engine;
        this.invokedFromParent = parent;
        this.invokedFromCall = call;

    }

    public static Invoker byUser() {
        return new Invoker(true, false, null, null);
    }

    public static Invoker byEngine() {
        return new Invoker(false, true, null, null);
    }

    public static Invoker byParentCommand(Command command) {
        return new Invoker(false, false, command, null);
    }

    public static Invoker byCommandCall(Command command) {
        return new Invoker(false, false, null, command);
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

    public Command getInvokedFromCall() {
        return invokedFromCall;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getString() {
        if (user || engine) {
            return user ? "USER" : "ENGINE";
        }

        return (invokedFromParent == null
                ? invokedFromCall.getClass().getSimpleName() + " [From call site]"
                : invokedFromParent.getClass().getSimpleName() + " [As a subcommand]");
    }
}
