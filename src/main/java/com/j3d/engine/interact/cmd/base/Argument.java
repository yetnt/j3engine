package com.j3d.engine.interact.cmd.base;

public interface Argument {
    String getName();
    String getDescription();
    boolean isOptional();
}
