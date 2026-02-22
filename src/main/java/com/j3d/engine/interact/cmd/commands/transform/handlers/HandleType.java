package com.j3d.engine.interact.cmd.commands.transform.handlers;

public enum HandleType {
    X, Y, Z;

    @Override
    public String toString() {
        return this.name() + " HandleType";
    }
}
