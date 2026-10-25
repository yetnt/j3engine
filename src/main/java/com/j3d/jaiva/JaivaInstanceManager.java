package com.j3d.jaiva;

import com.j3d.jaiva.packs.*;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.jaiva.JBundler;

public class JaivaInstanceManager {
    private final JBundler commandBundler;

    public JaivaInstanceManager() {
        commandBundler = new JBundler(TestPack.class, ObjectsPack.class, GettersPack.class, BasePack.class, NewPack.class, Vec3Pack.class);
    }

    public JBundler getCommandBundler() {
        return commandBundler;
    }
}
