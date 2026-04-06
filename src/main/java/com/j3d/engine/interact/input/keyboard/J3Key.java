package com.j3d.engine.interact.input.keyboard;

import javax.swing.*;
import java.util.UUID;

/**
 * A singular key on the keyboard or keystroke that gets put into {@link KeyBindings}
 */
public class J3Key {
    private String name;
    private final UUID id = UUID.randomUUID();
    private KeyStroke keyStroke;
    private Action oldAction = null;
    private Action action;
    private boolean actionReplaced = false;
    private boolean oneShot = false;

    public J3Key(String name, boolean oneShot) {
        this.name = name;
        this.oneShot = oneShot;
    }

    public J3Key(String name, KeyStroke keyStroke, Action action) {
        this.name = name;
        this.keyStroke = keyStroke;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public Action getAction() {
        return action;
    }

    public UUID getId() {
        return id;
    }

    public boolean isActionReplaced() {
        return actionReplaced;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public J3Key setName(String name) {
        this.name = name;
        return this;
    }

    public J3Key setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
        return this;
    }

    public J3Key setAction(Action action) {
        if (actionReplaced) oldAction = action;
        else this.action = action;
        return this;
    }

    public J3Key replaceAction(Action newAction) {
        if (oneShot) return this; // One shot keys cannot be replaced.
        oldAction = action;
        action = newAction;
        actionReplaced = true;
        return this;
    }

    public J3Key resetAction() {
        if (actionReplaced) {
            action = oldAction;
            actionReplaced = false;
        }
        return this;
    }
}
