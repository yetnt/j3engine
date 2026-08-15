package com.j3d.engine.interact.input.keyboard;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.StaticConfig;
import com.j3d.gen.settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static com.j3d.StaticRefs.getCamera;
import static com.j3d.engine.interact.input.keyboard.KeyBindings.commandPaletteFocusOwner;

/**
 * The default key binds within J3Engine.
 * @implSpec You should probably not change the default action of these...
 * @author Lehlogonolo Poole
 * @see KeyBindings
 * @see J3Key
 */
public enum GlobalKeybinds {

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
                            if (StaticRefs.getCommandParser().commandPalette.isDisabled()) return;
                            StaticRefs.getMainFrame().getCommandPalette()
                                    .inputField.requestFocusInWindow();
                        }
                    }
            )
    ),
    // DEFOCUS_COMMAND_PALETTE moved to the command palette input map.
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().add(
                                            getCamera().getForward().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().sub(
                                            getCamera().getForward().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().sub(
                                            getCamera().getRight().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().add(
                                            getCamera().getRight().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().add(
                                            getCamera().getUp().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
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
                            if (commandPaletteFocusOwner() || !StaticConfig.movementControls) return;
                            double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
                            getCamera().setPosition(
                                    getCamera().getPosition().sub(
                                            getCamera().getUp().mult(mvSpeed)
                                    )
                            );
                            StaticRefs.getMainFrame().repaint();
                        }
                    }
            )
    ),
    /**
     * Paired with {@link SelectionMouseOwner} where if the user holds down I while making a selection
     * it will switch to the subtract selection mode. This is then reset by {@link GlobalKeybinds#SELECT_SUBTRACT_UP}
     * @implSpec This is the leader of a 4 keychain link, the first child is {@link GlobalKeybinds#SELECT_SUBTRACT_UP}
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
     * @see GlobalKeybinds#SELECT_SUBTRACT_DOWN
     * @implSpec This is the child link to {@link GlobalKeybinds#SELECT_SUBTRACT_DOWN}
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
     * Paired with {@link SelectionMouseOwner} where if the user holds down U while making a selection
     * it will switch to the add selection mode. This is then reset by {@link GlobalKeybinds#SELECT_UNION_UP}
     */
    SELECT_UNION_DOWN(
            new J3Key(
                    "selectAdd",
                    KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, false),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.UNION;
                        }
                    }
            )
    ),
    /**
     * Wrapper of {@link KeyBindings#clearInferredSelectionType}
     */
    SELECT_UNION_UP(
            new J3Key(
                    "selectAddUp",
                    KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, true),
                    KeyBindings.clearInferredSelectionType
            )
    ),
    /**
     * Keystroke to hide the command pallete.
     */
    HIDECMDP(
            new J3Key(
                    "hideCommandPalleteKey",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            StaticRefs.getCommandParser().commandPalette.setDisabled(
                                    !StaticRefs.getCommandParser().commandPalette.isDisabled()
                            );
                            StaticRefs.getMainFrame().repaint();
                        }
                    }
            )
    ),
    SELECT_ALL(
            new J3Key(
                    "selectAll",
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            StaticRefs.getSceneManager().selectAll();
                            StaticRefs.getMainFrame().repaint();
                        }
                    }
            )
    ),
    CAPS_LOCK(
            new J3Key(
                    "shiftDown",
                    KeyStroke.getKeyStroke(KeyEvent.VK_CAPS_LOCK, 0, false),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            StaticConfig.lock = !StaticConfig.lock;
                        }
                    }
            )
    ),
    F1(
            new J3Key(
                    "f1",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            StaticRefs.getDocsProvider()
                                    .provideMain()
                                    .setVisible(true);
                        }
                    }
            )
    );




    private J3Key key;
    GlobalKeybinds(J3Key key) {
        this.key = key;
    }

    public J3Key getKey() {
        return key;
    }

}
