package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.input.keyboard.DefaultKeys;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.ui.J3DTheme;
import com.j3d.utility.JLabelRichText;

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
 * {@link Command#run(SafeJLabel, String, Object[], java.util.ArrayList)} is required to call {@link CommandsManager#setAsCurrent(StatefulCommand)}
 * so it sets itself. if this is not done {@link StatefulCommand#run(StatefulCommand, String, Object, SafeJLabel)} will exit early.
 * @param <T> The type of object that the command operates on.
 * @author Lehlogonolo Poole
 * @see CommandParser#run()
 * @see CommandsManager#setAsCurrent(StatefulCommand)
 */
public interface StatefulCommand<T> {
    /**
     * Called when the command is first started.
     * @param object The object that the command operates on.
     * @param label The SafeJLabel instance.
     */
    void onStart(T object, SafeJLabel label);

    /**
     * Called when the 'Enter' key is pressed.
     * @param e The event that triggered this method.
     * @param object The object that the command operates on.
     * @param label The SafeJLabel instance.
     */
    void onEnter(ActionEvent e, T object, SafeJLabel label);

    /**
     * Called when the 'Escape' key is pressed.
     * @param e The event that triggered this method.
     * @param object The object that the command operates on.
     * @param label The SafeJLabel instance.
     */
    void onEsc(ActionEvent e, T object, SafeJLabel label);

    /**
     * Runs the stateful command.
     * @param t The stateful command that is running.
     * @param name The name of the command.
     * @param object The object that the command should operates on.
     */
    default void run(StatefulCommand t, String name, T object, SafeJLabel label) {
        if (!CommandsManager.isCurrentStatefulRunning(t)) return;

        // if theres a selection, clear it.
        SelectionManager.selectionMouseOwner.clearSelectionSquare();

        label.setLower(
                JLabelRichText.htmlOf(
                        new JLabelRichText("hit ENTER to confirm command, otherwise escape using ESC")
                                .bold().italic().font(J3DTheme.TEXT_PRIMARY.color())
                )
        );

        Static.mainFrame.requestFocusInWindow(); // get out of the command window focus. very important
        Static.commandParser.disable(); // disable the command pallete input field.

        onStart(object, label); // Fire the starting stuff

        // keystroke for when they hit enter
        J3Key enter = new J3Key(name + "n", true)
                .setKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false))
                .setAction(
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                onEnter(e, object, label);
                                Static.mainFrame.repaint();
                                Static.commandParser.enable();
                                CommandsManager.clearCurrent();
                            }
                        });
        Static.keybinds.registerJ3Key(enter);

        // keystroke for when they bail with escape
        // Escape is already binded to something so it cant be a oneshot in practice
        // but we can replace it and immediately set it back to normal!
        DefaultKeys.DEFOCUS_COMMAND_PALETTE.getKey().replaceAction(
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!CommandsManager.isCurrentStatefulRunning(t)) return;
                        onEsc(e, object, label);
                        Static.keybinds.removeJ3Key(enter.getId());
                        Static.mainFrame.repaint();
                        Static.commandParser.enable();
                        CommandsManager.clearCurrent();
                        DefaultKeys.DEFOCUS_COMMAND_PALETTE.getKey().resetAction();
                    }
                }
        );
    }
}