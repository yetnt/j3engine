package com.j3d.engine.interact.input.keyboard;

import com.j3d.Static;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.generic.Pair;

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
    public KeyBindings(InputMap im, ActionMap am, boolean global) {
        inputMap = im;
        actionMap = am;

        if (global) {

            // TODO: Wherever keybinds can be changed Enforce only SELECT_SUBTRACT_DOWN as changeable and cannot have the SHIFT_DOWN_MASK as its links use it.
            GlobalKeybinds.SELECT_SUBTRACT_DOWN.getKey().linkTo(
                    GlobalKeybinds.SELECT_SUBTRACT_UP.getKey(),
                    0
            );
            GlobalKeybinds.SELECT_SUBTRACT_UP.getKey().linkTo(
                    GlobalKeybinds.SELECT_ADD_DOWN.getKey(),
                    KeyEvent.SHIFT_DOWN_MASK
            );
            GlobalKeybinds.SELECT_ADD_DOWN.getKey().linkTo(
                    GlobalKeybinds.SELECT_ADD_UP.getKey(),
                    KeyEvent.SHIFT_DOWN_MASK
            );

            for (GlobalKeybinds key : GlobalKeybinds.values()) {
                rJ3Key(
                        key.getKey()
                );
            }
        }
    }


    /**
     * Registers a J3Key. unsafely.
     * @implSpec This bypasses all the hecks and guard rails. Specifically for when they've already been
     * checked or if we're in {@link #KeyBindings(InputMap, ActionMap, boolean)}
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
            Static.getLog().error("Attempted to add duplicate J3Key: " + key.getName());
            return;
        }
        if (prohibited.contains(key.getKeyStroke())) {
            Static.getLog().error("Attempted to add prohibited key binding: " + key.getKeyStroke());
            return;
        }
        if (keys.stream().anyMatch(k -> k.getKeyStroke().equals(key.getKeyStroke()))) {
            Static.getLog().error("Attempted to add duplicate key binding: " + key.getKeyStroke());
            return;
        }
        if (keys.stream().anyMatch(k -> k.getName().equals(key.getName()))) {
            Static.getLog().error("Attempted to add duplicate key name: " + key.getName());
            return;
        }
        rJ3Key(key);
    }

    private void updateLinks(J3Key leader, KeyStroke newKeyStroke) {
        if (leader.getLink() == null) return;
        Pair<J3Key, Integer> child = leader.getLink();
        inputMap.remove(child.first.getKeyStroke());
        child.first.setKeyStroke(
                KeyStroke.getKeyStroke(
                        newKeyStroke.getKeyCode(),
                        child.second.intValue() | newKeyStroke.getModifiers(),
                        child.first.getKeyStroke().isOnKeyRelease()
                )
        );
        inputMap.put(child.first.getKeyStroke(), child.first.getId());
        updateLinks(child.first, newKeyStroke);
    }

    /**
     * Inspects whether the leader of a chain of keystroke's updated modifier
     * will clash or cause a clash with any 1 child keystroke.
     * <p>
     *     Preamble : {@link KeyEvent} are bit masks, meaning combinations are just the bitwise OR
     *     and two combinations such as {@code SHIFT | SHIFT} become {@code SHIFT}
     * </p>
     *      <p>
     *          In essence a conflict is made when the bitwise OR of the leader's modifiers with
     *          any child modifier is equal to the leader modifier.
     *          <pre>{@code
     *          (leaderMod | childMod) == leaderMod
     *          }</pre>
     *      </p>
     * <p>
     *     Say we have the following test cases, where the first keystroke is the leader and the rest are childrten.
     *     <pre>{@code
     *      1. (I) -> (SHIFT + I) -> (CTRL + I)
     *      2. (P) -> (ALT + P) -> (SHIFT + P)
     *      3. K -> (CTRL + K) -> (ALT + K)
     *      }</pre>
     *      And we'd like to rebind the leader of each link to {@code ALT + O}, {@code SHIFT + S} and {@code CTRL + ALT + J}
     *      respectively. (We Only care about the SHIFT, CTRL, ALT, ALT_GRADE and META modifiers, so the letters themselves
     *      will be ignored and replaced with a question mark to represent the letter)
     *      <p>
     *          In link 1 it becomes
     *          <pre>{@code
     *          (ALT + ?) -> (SHIFT + ALT + ?) -> (CTRL + ALT + ?)
     *          }</pre>
     *          No child's modifier conflicts with the leader key's modifier. This is a valid change.
     *      </p>
     *      <p>
     *          In link 2
     *          <pre>{@code
     *          (SHIFT + ?) -> (ALT + SHIFT + ?) -> (SHIFT + SHIFT + ?)
     *          // The 2 shifts simplify to a singular shift via bitwise OR
     *          (SHIFT + ?) -> (ALT + SHIFT + ?) -> (SHIFT + ?)
     *          }</pre>
     *          Here due to the simplification, the leader conflicts with the last child. This is an invalid
     *          case.
     *      </p>
     *      <p>
     *          In link 3
     *          <pre>{@code
     *          (CTRL + ALT + ?) -> (CTRL + CTRl + ALT + ?) -> (ALT + CTRL + ALT + ?)
     *          // Again the duplicate shifts OR together
     *          (CTRL + ALT + ?) -> (CTRL + ALT + ?) -> (CTRL + ALT + ?)
     *          }</pre>
     *          This link, obviously also produces conflicts, accept both children conflict with the parent node.
     *      </p>
     * </p>
     * And this method, simply checks for these.
     * @implNote Multiple children can reference
     * the same keystroke however the leader of the keychain must be unique from it's children.
     * And its important to know that if the {@link KeyStroke#isOnKeyRelease()} is different for both keys
     * they are classified as 2 different keys and do not trigger this method.
     * @param leader The leader in question.
     * @return Whether any child modifier of this key clash with said key or not.
     * @see KeyStroke
     * @see KeyEvent
     * @see J3Key
     */
    public static boolean childModifiersClash(J3Key leader) {
        if (leader.getLink() == null) return false;
        int leaderModifier = leader.getKeyStroke().getModifiers();

        J3Key child = leader.getLink().first;
        while (child != null) {
            if (
                    ((child.getKeyStroke().getModifiers() | leaderModifier) == leaderModifier)
                    && child.getKeyStroke().isOnKeyRelease() == leader.getKeyStroke().isOnKeyRelease()
            ) return true;
            child = child.getLink() == null ? null : child.getLink().first;
        }
        return false;
    }

    /**
     * Updates the keystroke of a J3Key. If an existing J3Key with the same keystroke is found,
     * it swaps the 2 keys.
     * @param old The old keystroke of this key.
     * @param key The key in question
     * @return An {@link UpdatedJ3Key} object that indicates the result of the update.
     */
    public UpdatedJ3Key rebindJ3KeyKeystroke(KeyStroke old, J3Key key) {
        if (prohibited.contains(key.getKeyStroke())) {
            Static.getLog().error("Attempted to add prohibited key binding: " + key.getKeyStroke());
            key.setKeyStroke(old);
            return new UpdatedJ3Key();
        }
        if (childModifiersClash(key)) {
            Static.getLog().error("Attempted to update a leader key with a new modifier that would cause a collision with a child key.");
            key.setKeyStroke(old);
            return new UpdatedJ3Key();
        }
        inputMap.remove(old);
        J3Key other = null;
        // since there is only ever a single key which can be matched, this while executes once.
        while (keys.stream().filter(k -> !k.getId().equals(key.getId())).anyMatch(k -> k.getKeyStroke().equals(key.getKeyStroke()))) {
            // swap, so set that J3Key to act on this old keystroke and same for the input map.
            J3Key dupeKey = keys.stream().filter(k -> k.getKeyStroke().equals(key.getKeyStroke())).findFirst().orElse(null);
            if (dupeKey == null) break;
            inputMap.remove(dupeKey.getKeyStroke());
            dupeKey.setKeyStroke(old);
            inputMap.put(old, dupeKey.getId());
            other = dupeKey;
            updateLinks(dupeKey, old);
        }
        inputMap.put(key.getKeyStroke(), key.getId());
        updateLinks(key, key.getKeyStroke());
        return new UpdatedJ3Key().success().swappedWith(other);
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

    public static boolean commandPaletteFocusOwner(CommandPalette cmdP) {
        return cmdP.inputField.isFocusOwner() && !Static.mainPanel.isFocusOwner();
    }

    /**
     * UpdatedJ3Key is a return object of {@link KeyBindings#rebindJ3KeyKeystroke(KeyStroke, J3Key)}
     * which represents the result of the update.
     * @author Lehlogonolo Poole
     * @see KeyBindings#rebindJ3KeyKeystroke(KeyStroke, J3Key)
     * @see J3Key
     */
    public static class UpdatedJ3Key {
        /**
         * Whether this key was swapped with an existing key of the same keystroke.
         */
        public boolean swapped = false;
        /**
         * If {@link UpdatedJ3Key#swapped} is true, this holds the J3Key of the other key.
         * Otherwise null.
         */
        public J3Key otherKey = null;
        /**
         * Whether the keystroke was successfully changed.
         */
        public boolean keyChangeSuccess = false;

        /**
         * Default constructor for {@link UpdatedJ3Key}.
         */
        public UpdatedJ3Key() {
        }

        /**
         * Marks the key as successfully changed.
         * @return The UpdatedJ3Key object for chaining.
         * @implSpec This is not for object readers to use only instantiators.
         */
        public UpdatedJ3Key success() {
            this.keyChangeSuccess = true;
            return this;
        }

        /**
         * Marks the key as swapped with another key
         * @param key the other key
         * @return The UpdatedJ3Key object for chaining.
         * @implSpec This is not for object readers to use only instantiators.
         */
        public UpdatedJ3Key swappedWith(J3Key key) {
            if (key == null) return this;
            this.swapped = true;
            this.otherKey = key;
            return this;
        }
    }

}
