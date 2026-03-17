package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.selection.SelectionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A command that can be in one of two states: running or not running.
 * When a stateful command is running, it takes over the input stream and waits for a specific event to occur.
 * This event can be either the 'Enter' key being pressed, which confirms the command and executes its 'onEnter' method,
 * or the 'Escape' key being pressed, which cancels the command and executes its 'onEsc' method.
 * @param <T> The type of object that the command operates on.
 */
public interface StatefulCommand<T> {
    /**
     * Called when the command is first started.
     */
    void onStart();

    /**
     * Called when the 'Enter' key is pressed.
     * @param e The event that triggered this method.
     * @param o The object that the command operates on.
     */
    void onEnter(ActionEvent e, T o);

    /**
     * Called when the 'Escape' key is pressed.
     * @param e The event that triggered this method.
     * @param o The object that the command operates on.
     */
    void onEsc(ActionEvent e, T o);

    /**
     * Runs the stateful command.
     * @param t The stateful command that is running.
     * @param name The name of the command.
     * @param o The object that the command should operates on.
     */
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