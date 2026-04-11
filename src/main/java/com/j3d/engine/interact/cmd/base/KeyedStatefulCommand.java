package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.geometry.constraints.*;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.AbstractTransform;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.OtherKeys;
import com.j3d.ui.util.SafeJLabel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface KeyedStatefulCommand extends StatefulCommand<Void> {
    String UP = "upArrowKey";
    String DOWN = "downArrowKey";
    String LEFT = "leftArrowKey";
    String RIGHT = "rightArrowKey";

    ArrayList<J3Key> getKeys();
    String selfName();
    ArrayList<Vector3> getOriginalPointPositions();
    ArrayList<GPoint> getReferences();
    double[] getGearTrain();
    int getGearIndex();
    void setGearIndex(int index);

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
     * Configures and registers a {@link J3Key} for a specific transformation direction (e.g., Up, Down, Left, Right).
     * This method encapsulates the complex logic of applying a transformation while
     * integrating the constraint validation system.
     *
     * @param key        The {@link KeyEvent} identifier for the key.
     * @param earlyExit  If this function returns true, the action is aborted.
     * @param shared     A {@link Supplier} that provides a shared variable (of type {@code T})
     *                   representing the magnitude or specific value of the transformation. This
     *                   value is used by both the constraint checker and the actual application
     *                   of the transform.
     * @param application A {@link Consumer} that applies the transformation to the actual
     *                    {@link GObject}s. This consumer is only executed if all constraints are satisfied.
     * @param biConsumer  A {@link BiConsumer} that applies the transformation to the
     *                    {@link ConstraintMirror} objects within a {@link ConstraintIntent}.
     *                    This is the "what-if" function used by the constraint system to check
     *                    if the proposed transform breaks any rules without modifying the original objects.
     * @param <T>        The type of the shared variable representing the transformation value.
     * @implSpec This method is not intended for direct use. Instead, specialized setter methods
     *           (e.g., {@code setUpKey()}, {@code setDownKey()}) should be implemented in
     *           subclasses, making use of the static key constants defined in {@link AbstractTransform}.
     */
    default <T> void setKey(int key, Function<T, Boolean> earlyExit, Supplier<T> shared, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        getKeys().add(
                new J3Key(
                        selfName() + KeyEvent.getKeyText(key) + "keyedOperation",
                        KeyStroke.getKeyStroke(
                                key
                                , 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                T sharedVar = shared.get();
                                if (earlyExit.apply(sharedVar)) return;
                                ArrayList<ConstraintMirror> c = ConstraintUtils.converter(
                                        getReferences().stream().map(o -> (GObject)o).collect(Collectors.toCollection(ArrayList::new))
                                );
                                ConstraintIntent intent = new ConstraintIntent(c,
                                        (mp) -> biConsumer.accept(sharedVar, mp)
                                );
                                for (GPoint ref : getReferences()) {
                                    boolean allConstr = ref.getConstraints().allSatisfied(
                                            "Cannot transform object due to " + SafeJLabel.EMPH,
                                            intent
                                    );
                                    if (!allConstr) return; // method above sent user UX
                                }
                                // if we make it here, apply evberything as normal.
                                application.accept(sharedVar);
                                getReferences().stream()
                                        .map(GPoint::getConstraints)
                                        .flatMap(ConstraintManager::constraintStream)
                                        .forEach(ConstraintOn::applyConstraint);
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );
    }

    default <T> void setUpKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(KeyEvent.VK_UP, earlyExit, shared, application, biConsumer);
    }
    default <T> void setDownKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(KeyEvent.VK_DOWN, earlyExit, shared, application, biConsumer);
    }
    default <T> void setLeftKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(KeyEvent.VK_LEFT, earlyExit, shared, application, biConsumer);
    }
    default <T> void setRightKey(Supplier<T> shared, Function<T, Boolean> earlyExit, Consumer<T> application, BiConsumer<T, HashMap<UUID, ConstraintMirror>> biConsumer) {
        setKey(KeyEvent.VK_RIGHT, earlyExit, shared, application, biConsumer);
    }
    default void setKeyAsSideEffects(int keyEvent, Runnable function) {
        setKey(keyEvent, (o) -> {
            function.run();
            return true;
        },
                ()-> null,
                (ignored)->{},
                (ignored, ignored1)->{}
        );
    }
}
