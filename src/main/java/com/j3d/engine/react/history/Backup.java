package com.j3d.engine.react.history;

import com.j3d.StaticRefs;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.CleanableAction;

import java.util.ArrayList;

public class Backup extends ArrayList<Action<?>> {
    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    public Backup() {
        super();
    }

    @Override
    public void clear() {
        for (Action<?> a : this) {
            if (a instanceof CleanableAction cl)
                try {
                    cl.cleanup();
                    StaticRefs.getLog().println(History.logHead + "Cleaned up (as a result of the backup being cleared) -> " + a.getDescription());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        super.clear();
    }
}
