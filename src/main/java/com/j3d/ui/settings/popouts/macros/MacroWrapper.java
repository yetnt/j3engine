package com.j3d.ui.settings.popouts.macros;

import javax.swing.*;

public class MacroWrapper {
    private final String identity;
    private KeyStroke keyStroke;

    public MacroWrapper(String identity, KeyStroke keyStroke) {
        this.identity = identity;
        this.keyStroke = keyStroke;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public String getIdentity() {
        return identity;
    }

    public void setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }
}
