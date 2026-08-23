package com.j3d.jaiva;

import com.j3d.jaiva.packs.getters.GettersPack;
import com.j3d.jaiva.packs.ObjectsPack;
import com.j3d.jaiva.packs.TestPack;
import com.jaiva.JBundler;

public class JaivaInstanceManager {
    private final JBundler commandBundler;

    public JaivaInstanceManager() {
        commandBundler = new JBundler(TestPack.class, ObjectsPack.class, GettersPack.class);
    }

    public JBundler getCommandBundler() {
        return commandBundler;
    }
}
