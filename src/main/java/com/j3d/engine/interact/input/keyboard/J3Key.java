package com.j3d.engine.interact.input.keyboard;

import com.j3d.utility.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.UUID;

/**
 * A singular key on the keyboard or keystroke that gets put into {@link KeyBindings}.
 * A J3Key can be identified within {@link KeyBindings#getActionMap()} or {@link KeyBindings#getInputMap()}
 * via it's UUID.
 * <p>
 *     This can either be a keystroke to do one task, which can have its action switched out for
 *     something else temporarily. Say if you have some key which works on W and you'd like to switch
 *     it to work for something else and then switch it back to normal.
 * </p>
 * <p>
 *     A J3Key can also represent a one shot key where it deregisters itself after clicking.
 * </p>
 * <p>
 *     A J3key can also be linked to other keys. See {@link DefaultKeys#SELECT_SUBTRACT_DOWN}
 *     for example which is the leader of a 4 keychain link. If it's {@code I} keybind is changed,
 *     the children will also change along with their own respective keycode.
 *     e.g. if {@code I} changes to {@code G+SHIFT}, and it was linked to {@code I+CTRL},
 *     that will change to the new parent's code plus the child's original modifiers {@code G+SHIFT+CTRL}.
 *     In a chain the leader's {@link KeyEvent} is what will cascade and not a child's parent.
 * </p>
 * @see KeyBindings
 * @see J3Key
 * @see KeyStroke
 * @see Action
 * @see ActionEvent
 * @see InputMap
 * @see ActionMap
 * @author Lehlogonolo Poole
 */
public class J3Key {
    private String name;
    private final UUID id = UUID.randomUUID();
    private KeyStroke keyStroke;
    private Action oldAction = null;
    private Action action;
    private boolean actionReplaced = false;
    private boolean oneShot = false;
    /**
     * If this key has a normal version and is paired with another,
     * this stores the key object, and it's modifiers.
     */
    private Pair<J3Key, Integer> link;

    /**
     * Default J3Key constructor
     * @implSpec Due to this constructor not applying the actual keystroke and action, you may need to use
     * the setters which allow for method chaining.
     * @param name the name of the key
     * @param oneShot whether the key is only executed once before being removed.
     */
    public J3Key(String name, boolean oneShot) {
        this.name = name;
        this.oneShot = oneShot;
    }

    /**
     * J3Key Constructor with all parameters.
     * @param name the name of the key
     * @param keyStroke the keystroke to bind to
     * @param action the action to perform when the keystroke is pressed
     */
    public J3Key(String name, KeyStroke keyStroke, Action action) {
        this.name = name;
        this.keyStroke = keyStroke;
        this.action = action;
    }

    /**
     * Marks another key as the child link of this key. Such that if this key were updated, the other key is
     * also updated.
     * @param otherKey the other key
     * @param otherKeyModifiers the other key's modifiers
     * @return this key for method chaining
     * @implSpec Avoid using key linking unless you know for certain updating this key won't misconstrue
     * with updating its pair. E.g. a key pair {@code P} and {@code CTRL+P}, where changing {@code P}
     * to {@code S}, will change {@code CTRL+P} to {@code CTRL+S} which is prohibited and not checked
     * under {@link KeyBindings}. Unless really needed, avoid using pairs.
     */
    public J3Key linkTo(J3Key otherKey, int otherKeyModifiers) {
        this.link = new Pair<>(otherKey, otherKeyModifiers);
        return this;
    }

    /**
     * Returns the name of the key.
     * @return the name of the key
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the keystroke of the key.
     * @return the keystroke of the key
     */
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    /**
     * Returns the action of the key.
     * @implNote This returns the current action, so if replaced it will return the new action.
     * @return the action of the key
     */
    public Action getAction() {
        return action;
    }

    /**
     * Returns the id of the key used for identification within {@link KeyBindings#getActionMap()}
     * and {@link KeyBindings#getInputMap()}.
     * @return the id of the key
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the child link of this key.
     * @return the child link of this key and its modifiers.
     */
    public Pair<J3Key, Integer> getLink() {
        return link;
    }

    /**
     * Whether the action of this key was replaced temporarily.
     * @return whether the action of this key was replaced temporarily
     */
    public boolean isActionReplaced() {
        return actionReplaced;
    }

    /**
     * Whether this key will only execute once or not.
     * @return whether this key will only execute once or not
     */
    public boolean isOneShot() {
        return oneShot;
    }

    /**
     * Sets the name of the key.
     * @implSpec This is only for method chaining and otherwise the name should be treated as immutable.
     * If updated it will NOT reflect in {@link KeyBindings}
     * @param name the name of the key
     * @return this key for method chaining
     */
    public J3Key setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the keystroke of this key
     * @param keyStroke the keystroke to bind to
     * @return this key for method chaining
     * @implSpec This only updates the object. You need to pass through {@link KeyBindings#rebindJ3KeyKeystroke(KeyStroke, J3Key)}
     * for the update to cascade, and if the keystroke is prohibited, te method may rollback this choice.
     */
    public J3Key setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
        return this;
    }

    /**
     * Sets the action of this key.
     * @param action the action to perform when the keystroke is pressed
     * @return this key for method chaining
     * @implNote This sets the original action. if replaced the replaced action stays intact and then when
     * reset to normal, this new value will be used.
     */
    public J3Key setAction(Action action) {
        if (actionReplaced) oldAction = action;
        else this.action = action;
        return this;
    }

    /**
     * Replaces this key's original action with a new temporary action
     * @param newAction The new action
     * @return this key for method chaining
     * @implNote use {@link J3Key#resetAction} to undo this
     */
    public J3Key replaceAction(Action newAction) {
        if (oneShot) return this; // One shot keys cannot be replaced.
        oldAction = action;
        action = newAction;
        actionReplaced = true;
        return this;
    }

    /**
     * Resets the action, if replaced, to its normal value.
     * @return this key for method chaining
     */
    public J3Key resetAction() {
        if (actionReplaced) {
            action = oldAction;
            actionReplaced = false;
        }
        return this;
    }

    /**
     * Calls the current action.
     * @param e the action event
     * @implSpec This is only called within {@link KeyBindings#registerJ3Key(J3Key)} so you not need
     * focus on this.
     */
    public void call(ActionEvent e) {
        action.actionPerformed(e);
    }
}
