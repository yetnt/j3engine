package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.selection.SelectionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A command that can be in one of two states: running or not running.
 * When a stateful command is running, it takes over the input stream and waits for a specific event to occur.
 * This event can be either the 'Enter' key being pressed, which confirms the command and executes its 'onEnter' method,
 * or the 'Escape' key being pressed, which cancels the command and executes its 'onEsc' method.
 * @implSpec While {@link CommandParser#run()} does the handling of stateful commands
 * as in registering which stateful command is running and disallowing other commands to run,
 * subcommands which are defined as stateful by design don't parse through that method. Therefore
 * in order to stay consistent with only 1 single stateful command running, the command itself within its
 * {@link Command#run(SafeJLabel, String, Object...)} is required to call {@link CommandsManager#setAsCurrent(StatefulCommand)}
 * so it sets itself. if this is not done {@link StatefulCommand#run(StatefulCommand, String, Object)} will exit early.
 * @param <T> The type of object that the command operates on.
 * @author Lehlogonolo Poole
 * @see CommandParser#run()
 * @see CommandsManager#setAsCurrent(StatefulCommand)
 */
public interface StatefulCommand<T> {
    /**
     * Called when the command is first started.
     * @param o The object that the command operates on.
     */
    void onStart(T o);

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

        onStart(o); // Fire the starting stuff

        // keystroke for when they hit enter
        J3Key enter = new J3Key(name + "n", true)
                .setKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false))
                .setAction(
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                onEnter(e, o);
                                Static.mainFrame.repaint();
                                CommandsManager.clearCurrent();
                            }
                        });
        Static.keybinds.registerJ3Key(enter);

        // keystroke for when they bail with escape
        J3Key esc = new J3Key(name + "esc", true)
                .setKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false))
                .setAction(
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (!CommandsManager.isCurrentStatefulRunning(t)) return;
                                onEsc(e, o);
                                Static.keybinds.removeJ3Key(enter.getId());
                                Static.mainFrame.repaint();
                                CommandsManager.clearCurrent();
                            }
                        });
        Static.keybinds.registerJ3Key(esc);
    }
}