package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.selection.SelectionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public interface StatefulCommand<T> {
    void onStart();
    void onEnter(ActionEvent e, T o);
    void onEsc(ActionEvent e, T o);

    default void run(StatefulCommand t, String name, T o) {
        if (!CommandsManager.isCurrentStatefulRunning(t)) return;

        // if theres a selection, clear it.
        SelectionManager.selectionMouseOwner.clearSelectionSquare();

        Static.mainFrame.requestFocusInWindow(); // get out of the command window focus. very important

        onStart(); // Fire the starting stuff

        // keystroke for when they hit enter
        KeyStroke enter = Static.keybinds.addOneShotKeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                name + "n",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onEnter(e, o);
                        Static.mainFrame.repaint();
                        CommandsManager.clearCurrent();
                    }
                }
        );

        // keystroke for when they bail with escape
        KeyStroke esc = Static.keybinds.addOneShotKeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                name + "esc",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!CommandsManager.isCurrentStatefulRunning(t)) return;
                        System.out.println("Le");;
                        onEsc(e, o);
                        Static.keybinds.removeKeyBinding(enter);
                        Static.mainFrame.repaint();
                        CommandsManager.clearCurrent();
                    }
                }
        );
    }
}


// why do you just randomly start thinking this?