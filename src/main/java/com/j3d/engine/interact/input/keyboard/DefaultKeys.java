package com.j3d.engine.interact.input.keyboard;

import com.j3d.Static;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.gen.settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.j3d.Static.camera;
import static com.j3d.engine.interact.input.keyboard.KeyBindings.commandPaletteFocusOwner;
import static com.j3d.ui.engine.EngineFrame.COMMAND_PALETTE;

/**
 * The default key binds within J3Engine.
 * @implSpec You should probably not change the default action of these...
 * @author Lehlogonolo Poole
 * @see KeyBindings
 * @see J3Key
 */
public enum DefaultKeys {

    /**
     * keystroke to directly focus into the command palette.
     */
    FOCUS_COMMAND_PALETTE(
            new J3Key(
                    "focusCommandPalette",
                    KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (Static.commandParser.commandPalette.isDisabled()) return;
                            COMMAND_PALETTE.inputField.requestFocusInWindow();
                        }
                    }
            )
    ),
    /**
     * keystroke to escape out of the command palette's focus.
     */
    DEFOCUS_COMMAND_PALETTE(
            new J3Key(
                    "defocusCommandPalette",
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (COMMAND_PALETTE.inputField.isFocusOwner()) {
                                if (Static.commandParser.commandPalette.isDisabled()) return;
                                Static.mainFrame.requestFocusInWindow();
                            }
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera forward (along the forward vector).
     */
    MOVE_CAM_FORWARD(
            new J3Key(
                    "moveCameraFoward",
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().add(
                                            camera.getForward().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera backward (along the forward vector).
     */
    MOVE_CAM_BACKWARD(
            new J3Key(
                    "moveCameraBackward",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().sub(
                                            camera.getForward().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera left (along the right vector).
     */
    MOVE_CAMERA_LEFT(
            new J3Key(
                    "moveCameraLeft",
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().sub(
                                            camera.getRight().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera right (along the right vector).
     */
    MOVE_CAMERA_RIGHT(
            new J3Key(
                    "moveCameraRight",
                    KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().add(
                                            camera.getRight().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera up (along the up vector).
     */
    MOVE_CAMERA_UP(
            new J3Key(
                    "moveCameraUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().add(
                                            camera.getUp().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * keystroke to move the camera down (along the up vector).
     */
    MOVE_CAMERA_DOWN(
            new J3Key(
                    "moveCameraDown",
                    KeyStroke.getKeyStroke(KeyEvent.VK_E, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            camera.setPosition(
                                    camera.getPosition().sub(
                                            camera.getUp().mult(mvSpeed)
                                    )
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    ),
    /**
     * Paired with {@link SelectionMouseOwner} where if the user holds down I while making a selection
     * it will switch to the subtract selection mode. This is then reset by {@link DefaultKeys#SELECT_SUBTRACT_UP}
     * @implSpec This is the leader of a 4 keychain link, the first child is {@link DefaultKeys#SELECT_SUBTRACT_UP}
     */
    SELECT_SUBTRACT_DOWN(
            new J3Key(
                    "selectSubtract",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.SUBTRACT;
                        }
                    }
            )
    ),
    /**
     * Wrapper of {@link KeyBindings#clearInferredSelectionType}
     * @see DefaultKeys#SELECT_SUBTRACT_DOWN
     * @implSpec This is the child link to {@link DefaultKeys#SELECT_SUBTRACT_DOWN}
     *
     */
    SELECT_SUBTRACT_UP(
            new J3Key(
                    "selectSubtractUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, true),
                    KeyBindings.clearInferredSelectionType
            )
    ),
    /**
     * Paired with {@link SelectionMouseOwner} where if the user holds down Shift + I while making a selection
     * it will switch to the add selection mode. This is then reset by {@link DefaultKeys#SELECT_ADD_UP}
     * @implSpec This is a child link to {@link DefaultKeys#SELECT_SUBTRACT_UP}
     * @see DefaultKeys#SELECT_SUBTRACT_DOWN
     */
    SELECT_ADD_DOWN(
            new J3Key(
                    "selectAdd",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK, false),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.ADD;
                        }
                    }
            )
    ),
    /**
     * Wrapper of {@link KeyBindings#clearInferredSelectionType}
     * @see DefaultKeys#SELECT_ADD_DOWN
     * @see DefaultKeys#SELECT_SUBTRACT_DOWN
     * @implSpec This is a child link to {@link DefaultKeys#SELECT_ADD_DOWN} and is the last child in the
     * {@link DefaultKeys#SELECT_ADD_DOWN} chain.
     */
    SELECT_ADD_UP(
            new J3Key(
                    "selectAddUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK, true),
                    KeyBindings.clearInferredSelectionType
            )
    ),
    HIDECMDP(
            new J3Key(
                    "hideCommandPalleteKey",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Static.commandParser.commandPalette.setDisabled(
                                    !Static.commandParser.commandPalette.isDisabled()
                            );
                            Static.mainFrame.repaint();
                        }
                    }
            )
    );

    private J3Key key;
    DefaultKeys(J3Key key) {
        this.key = key;
    }

    public J3Key getKey() {
        return key;
    }

}
