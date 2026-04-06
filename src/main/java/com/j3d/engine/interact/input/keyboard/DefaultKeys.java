package com.j3d.engine.interact.input.keyboard;

import com.j3d.Static;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.j3d.Static.camera;
import static com.j3d.engine.interact.input.keyboard.KeyBindings.commandPaletteFocusOwner;
import static com.j3d.ui.engine.EngineFrame.commandPallete;

public enum DefaultKeys {
    FOCUS_COMMAND_PALETTE(
            new J3Key(
                    "focusCommandPalette",
                    KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            commandPallete.inputField.requestFocusInWindow();
                        }
                    }
            )
    ),
    DEFOCUS_COMMAND_PALETTE(
            new J3Key(
                    "defocusCommandPalette",
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPallete.inputField.isFocusOwner()) {
                                Static.mainFrame.requestFocusInWindow();
                            }
                        }
                    }
            )
    ),
    MOVE_CAM_FORWARD(
            new J3Key(
                    "moveCameraFoward",
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    MOVE_CAM_BACKWARD(
            new J3Key(
                    "moveCameraBackward",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    MOVE_CAMERA_LEFT(
            new J3Key(
                    "moveCameraLeft",
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    MOVE_CAMERA_RIGHT(
            new J3Key(
                    "moveCameraRight",
                    KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    MOVE_CAMERA_UP(
            new J3Key(
                    "moveCameraUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    MOVE_CAMERA_DOWN(
            new J3Key(
                    "moveCameraDown",
                    KeyStroke.getKeyStroke(KeyEvent.VK_E, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (commandPaletteFocusOwner(commandPallete)) return;
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
    SELECT_SUBTRACT_UP(
            new J3Key(
                    "selectSubtractUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, true),
                    KeyBindings.clearInferredSelectionType
            )
    ),
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
    SELECT_ADD_UP(
            new J3Key(
                    "selectAddUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK, true),
                    KeyBindings.clearInferredSelectionType
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
