package com.j3d.jaiva;

import com.j3d.jaiva.packs.*;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.jaiva.JBundler;

public class JaivaInstanceManager {
    private final JBundler commandBundler;

    public JaivaInstanceManager() {
        commandBundler = new JBundler("com.j3d.jaiva.packs");
    }

    public JBundler getCommandBundler() {
        return commandBundler;
    }
}
