package com.j3d.engine.geometry.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class ConstraintIntent {
    private final HashMap<UUID, ConstraintMirror> affectedMirrors = new HashMap<>();
    private final Consumer<HashMap<UUID, ConstraintMirror>> update;

    public ConstraintIntent(ArrayList<ConstraintMirror> affected, Consumer<HashMap<UUID, ConstraintMirror>> update) {
        this.update = update;
        affected.forEach(mirror -> affectedMirrors.put(mirror.getId(), mirror));
    }

    public void consume() {
        update.accept(affectedMirrors);
    }

    public HashMap<UUID, ConstraintMirror> map() {
        return affectedMirrors;
    }
}
