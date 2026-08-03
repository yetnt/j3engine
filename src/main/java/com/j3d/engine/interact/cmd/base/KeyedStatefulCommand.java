package com.j3d.engine.interact.cmd.base;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.interact.cmd.commands.transform.AbstractTransform;
import com.j3d.engine.interact.cmd.commands.transform.TranslateSelection;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.OtherKeys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An extension of {@link StatefulCommand} for commands that are interactive and primarily
 * driven by a set of temporary, context-specific keybindings, such as the arrow keys.
 * <p>
 *      This interface defines the contract for a command that enters a "state" where it
 *      listens for specific key presses (like Up, Down, Left, Right) to manipulate a selection
 *      of objects. It provides a powerful, reusable framework for defining these key actions
 *      while seamlessly integrating with the application's constraint validation system.
 * </p>
 * A typical implementation, like {@link AbstractTransform}, will use this interface to
 * manage the lifecycle of temporary keybindings, applying transformations incrementally
 * and ensuring all geometric constraints are satisfied before committing any changes.
 *
 * @author Lehlogonolo Poole
 * @see StatefulCommand
 * @see AbstractTransform
 * @see TranslateSelection
 */
public interface KeyedStatefulCommand extends StatefulCommand<Void> {
    /**
     * Gets the list of temporary {@link J3Key}s that are active during this command's state.
     * @return An {@link ArrayList} of active keys.
     */
    ArrayList<J3Key> getKeys();

    /**
     * Returns the name of the specific command instance, used for creating unique key IDs.
     * @return The name of the command.
     */
    String selfName();

    /**
     * Gets the array of available step sizes for the transformation (the "gear train").
     * @return An array of doubles representing different step magnitudes.
     */
    double[] getGearTrain();

    /**
     * Gets the index of the currently active step size in the gear train.
     * @return The current gear index.
     */
    int getGearIndex();

    /**
     * Sets the current gear index, allowing the command to cycle through step sizes.
     * @param index The new gear index.
     */
    void setGearIndex(int index);

    /**
     * A factory method to create a new "gear" key for cycling through step sizes.
     * @param name The base name for the key.
     * @return A new {@link J3Key} configured to change the gear index.
     */
    default J3Key newGearKey(String name) {
        return new J3Key(
                name + "gearKey",
                OtherKeys.TRANSFORM_CHANGE_STEP_SIZE.getKeyStroke(),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setGearIndex((getGearIndex() + 1) % getGearTrain().length);
                    }
                }
        );
    }

    /**
     * Configures and registers a {@link J3Key} for a specific transformation action.
     * <p>
     * This is the core helper method of the interface. It encapsulates the entire
     * "check-then-apply" logic: it creates a keybinding that, when triggered, will
     * first validate the proposed transformation against all relevant constraints and,
     * only if validation succeeds, apply the transformation to the real objects.
     *
     * @param key         The {@link KeyEvent} virtual key code for the key (e.g., {@code KeyEvent.VK_UP}).
     * @param earlyExit   A function that is called before any processing. If it returns {@code true},
     *                    the entire action is aborted. Useful for state-dependent logic.
     * @param shared      A {@link Supplier} that provides a shared variable (of type {@code T})
     *                    representing the transformation's value (e.g., the distance to move).
     * @param application A {@link Consumer} that applies the transformation to the "real" {@link GObject}s.
     *                    This is only executed if all constraints are satisfied.
     * @param <T>         The type of the shared variable representing the transformation value.
     */
    default <T> void setKey(int key, Function<T, Boolean> earlyExit, Supplier<T> shared, Consumer<T> application) {
        getKeys().add(
                new J3Key(
                        selfName() + KeyEvent.getKeyText(key) + "keyedOperation",
                        KeyStroke.getKeyStroke(key, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                T sharedVar = shared.get();
                                if (earlyExit.apply(sharedVar)) return;

                                application.accept(sharedVar);

                                StaticRefs.getMainPanel().repaint();
                            }
                        }
                )
        );
    }

    /**
     * A convenience method to configure the 'Up Arrow' key.
     */
    default <T> void setUpKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application) {
        setKey(KeyEvent.VK_UP, earlyExit, shared, application);
    }
    /**
     * A convenience method to configure the 'Down Arrow' key.
     */
    default <T> void setDownKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application) {
        setKey(KeyEvent.VK_DOWN, earlyExit, shared, application);
    }
    /**
     * A convenience method to configure the 'Left Arrow' key.
     */
    default <T> void setLeftKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application) {
        setKey(KeyEvent.VK_LEFT, earlyExit, shared, application);
    }
    /**
     * A convenience method to configure the 'Right Arrow' key.
     */
    default <T> void setRightKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application) {
        setKey(KeyEvent.VK_RIGHT, earlyExit, shared, application);
    }

    /**
     * Configures a key to execute a simple {@link Runnable} as a side effect, without involving
     * the constraint system or any transformations.
     *
     * @param keyEvent The {@link KeyEvent} virtual key code for the key.
     * @param function The {@link Runnable} to execute when the key is pressed.
     */
    default void setKeyAsSideEffects(int keyEvent, Runnable function) {
        setKey(keyEvent, (o) -> {
            function.run();
            return true; // Use earlyExit to run the function and then immediately abort.
        },
                ()-> null,
                (ignored)->{}
        );
    }
}
