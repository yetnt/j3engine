package com.j3d.engine.interact.input.keyboard;

import com.j3d.Static;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.ui.engine.CommandPallete;
import com.j3d.ui.engine.EngineFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A class that manages the key bindings for the application. It allows for easy addition and removal
 * of key bindings, and handles the actions associated with each key binding.
 * @implSpec This does not include accelerators defined by {@link EngineFrame}.
 * Those are keybinds which cannot be changed and are listed within {@link KeyBindings#prohibited}
 * @author Lehlogonolo Poole
 * @see EngineFrame
 * @see J3Key
 * @see InputMap
 * @see ActionMap
 */
public class KeyBindings {

    private final InputMap inputMap;
    private final ActionMap actionMap;

    /**
     * List of registered keys.
     */
    private ArrayList<J3Key> keys = new ArrayList<>();

    /**
     * A list of prohibited key bindings that should not be added to the input and action maps. This is used to prevent
     * common key bindings that would interfere with the application's functionality from being added by the user.
     * <p>
     *     These are defined by {@link com.j3d.ui.engine.EngineFrame#jMenuBar1} using accelerators.
     * </p>
     */
    private ArrayList<KeyStroke> prohibited = new ArrayList<>(
            List.of(
                    // TODO: Programmatically go through the JMenuBar and make this list.
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), // Copy
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), // Paste
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), // Cut
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), // Undo
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), // Redo
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), // Save
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), // Open
                    KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), // New
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK),  // Settings
                    KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK),// Redraw
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK),// Reset Cam
                    KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK),// Reset Position
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.SHIFT_DOWN_MASK),// Reset Orientation
                    // Export Scene as PNG
                    KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)

            )
    );

    /** Initialises the key bindings for the application.
     * @param im the input map to use for key bindings
     * @param am the action map to use for key bindings
     */
    public KeyBindings(InputMap im, ActionMap am) {
        inputMap = im;
        actionMap = am;

        for (DefaultKeys key : DefaultKeys.values()) {
            rJ3Key(
                    key.getKey()
            );
        }
    }


    /**
     * Registers a J3Key. unsafely.
     * @implSpec This bypasses all the hecks and guard rails. Specifically for when they've already been
     * checked or if we're in {@link #KeyBindings(InputMap, ActionMap)}
     * where we're implementing the default trusted keys.
     * @param key the J3Key to register
     */
    private void rJ3Key(J3Key key) {
        keys.add(key);
        inputMap.put(key.getKeyStroke(), key.getId());
        actionMap.put(
                key.getId(),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        key.call(e);
                        if (key.isOneShot()) removeJ3Key(key.getId());
                    }
                });
    }

    /**
     * Registers a J3Key
     * @param key the J3Key to register
     */
    public void registerJ3Key(J3Key key) {
        if (keys.contains(key)) {
            Static.log.error("Attempted to add duplicate J3Key: " + key.getName());
            return;
        }
        if (prohibited.contains(key.getKeyStroke())) {
            Static.log.error("Attempted to add prohibited key binding: " + key.getKeyStroke());
            return;
        }
        if (keys.stream().anyMatch(k -> k.getKeyStroke().equals(key.getKeyStroke()))) {
            Static.log.error("Attempted to add duplicate key binding: " + key.getKeyStroke());
            return;
        }
        if (keys.stream().anyMatch(k -> k.getName().equals(key.getName()))) {
            Static.log.error("Attempted to add duplicate key name: " + key.getName());
            return;
        }
        rJ3Key(key);
    }

    /**
     * Removes a J3Key
     * @param id the id of the J3Key to remove
     * @return the J3Key that was removed
     */
    public J3Key removeJ3Key(UUID id) {
        J3Key key = keys.stream().filter(k -> k.getId().equals(id)).findFirst().orElse(null);
        if (key == null) return null;
        keys.remove(key);
        inputMap.remove(key.getKeyStroke());
        actionMap.remove(key.getId());
        return key;
    }

    /** Returns the action map containing the actions bound to keys.
     * @return the action map
     */
    public ActionMap getActionMap() {
        return actionMap;
    }

    /** Returns the input map containing the key bindings.
     * @return the input map
     */
    public InputMap getInputMap() {
        return inputMap;
    }

    public static AbstractAction clearInferredSelectionType = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.NONE;
        }
    };

    public static boolean commandPaletteFocusOwner(CommandPallete cmdP) {
        return cmdP.inputField.isFocusOwner();
    }

}
