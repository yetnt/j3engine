package com.j3d.engine.interact.input.keyboard;

import com.j3d.engine.interact.cmd.commands.transform.AbstractTransform;
import com.j3d.engine.interact.cmd.commands.transform.RotateSelection;
import com.j3d.engine.interact.cmd.commands.transform.ScaleSelection;
import com.j3d.engine.interact.cmd.commands.transform.TranslateSelection;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Any key defintion not defined within {@link DefaultKeys} for whatever reason.
 * This is just for clarity to know what keybinds are what.
 * @author Lehlogonolo Poole
 * @see DefaultKeys
 * @see KeyBindings
 */
public enum OtherKeys {
    /**
     * The key to toggle the transform step size to another pre-defined value within an implementor
     * of {@link AbstractTransform}. This is a temporary key that only exists when one of the
     * transform subcommands is currently running. Otherwise {@link KeyEvent#VK_R} is not constant.
     * @see AbstractTransform
     * @see ScaleSelection
     * @see RotateSelection
     * @see TranslateSelection
     */
    TRANSFORM_CHANGE_STEP_SIZE(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));

    private KeyStroke keyStroke;
    OtherKeys(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}
