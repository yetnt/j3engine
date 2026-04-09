package com.j3d.engine.geometry.constraints;

import java.util.UUID;

public interface ConstraintMirror {
    boolean isStale();
    UUID getId();
}
