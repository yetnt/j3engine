package com.j3d.engine.interact.input.keyboard;

import com.j3d.Static;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.ui.engine.CommandPallete;

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
 */
public class KeyBindings {

    static AbstractAction clearInferredSelectionType = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.NONE;
        }
    };

    private final InputMap inputMap;
    private final ActionMap actionMap;

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
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), // Copy
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), // Paste
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), // Cut
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), // Undo
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK)  // Redo
            )
    );

    public void registerJ3Key(J3Key key) {
        if (keys.contains(key)) return;
        if (prohibited.contains(key.getKeyStroke())) {
            Static.log.error("Attempted to add prohibited key binding: " + key.getKeyStroke());
            return;
        }
        keys.add(key);
        inputMap.put(key.getKeyStroke(), key.getId());
        actionMap.put(
                key.getId(),
                key.isOneShot() ? new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        key.getAction().actionPerformed(e);
                        removeJ3Key(key.getId());
                    }
                } : key.getAction());
    }

    public J3Key removeJ3Key(UUID id) {
        J3Key key = keys.stream().filter(k -> k.getId().equals(id)).findFirst().orElse(null);
        if (key == null) return null;
        keys.remove(key);
        inputMap.remove(key.getKeyStroke());
        actionMap.remove(key.getId());
        return key;
    }

    /** Initialises the key bindings for the application.
     * @param im the input map to use for key bindings
     * @param am the action map to use for key bindings
     * @param cmdP the command palette to focus/defocus with key bindings
     */
    public KeyBindings(InputMap im, ActionMap am, CommandPallete cmdP) {
        inputMap = im;
        actionMap = am;

        for (DefaultKeys key : DefaultKeys.values()) {
            registerJ3Key(
                    key.getKey()
            );
        }

//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "focusCommandPallete");
//        am.put("focusCommandPallete", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                cmdP.inputField.requestFocusInWindow();
//            }
//        });
//        registerJ3Key(
//                new J3Key(
//                        "focusCommandPallete",
//                        KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),
//                        new AbstractAction() {
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                cmdP.inputField.requestFocusInWindow();
//                            }
//                        }
//        );
//
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "defocusCommandPallete");
//        am.put("defocusCommandPallete", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (cmdP.inputField.isFocusOwner()) {
//                    Static.mainFrame.requestFocusInWindow();
//                }
//            }
//        });
//
//        // WASD and QE for camera movement
//
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveCameraForward");
//        am.put("moveCameraForward", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().add(
//                                camera.getForward().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveCameraBackward");
//        am.put("moveCameraBackward", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().sub(
//                                camera.getForward().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveCameraLeft");
//        am.put("moveCameraLeft", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().sub(
//                                camera.getRight().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveCameraRight");
//        am.put("moveCameraRight", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().add(
//                                camera.getRight().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "moveCameraUp");
//        am.put("moveCameraUp", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().add(
//                                camera.getUp().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "moveCameraDown");
//        am.put("moveCameraDown", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (commandPaletteFocusOwner(cmdP)) return;
//                double mvSpeed = Settings.cameraProperties.movementSpeed.getValue();
//                camera.setPosition(
//                        camera.getPosition().sub(
//                                camera.getUp().mult(mvSpeed)
//                        )
//                );
//                Static.mainFrame.repaint();
//            }
//        });
//
//        // I selection
//
//        AbstractAction clearInferredSelectionType = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.NONE;
//            }
//        };
//
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false), "selectSubtract");
//        am.put("selectSubtract", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.SUBTRACT;
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, true), "selectSubtractUp");
//        am.put("selectSubtractUp", clearInferredSelectionType);
//
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK, false), "selectAdd");
//        am.put("selectAdd", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.ADD;
//            }
//        });
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK, true), "selectAddUp");
//        am.put("selectAddUp", clearInferredSelectionType);
    }

    public static boolean commandPaletteFocusOwner(CommandPallete cmdP) {
        return cmdP.inputField.isFocusOwner();
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

    /**
     * Adds a key binding to the input and action maps.
     * @param keyStroke the keystroke to bind
     * @param actionName the name of the action to bind (used as the key in the action map)
     * @param action the action to perform when the keystroke is pressed
     */
    public void addKeyBinding(KeyStroke keyStroke, String actionName, Action action) {
        if (prohibited.contains(keyStroke)) {
            Static.log.error("Attempted to add prohibited key binding: " + keyStroke);
            return;
        }
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, action);
    }

    /**
     * Removes a key binding from the input and action maps.
     * @param keyStroke the keystroke to unbind
     */
    public void removeKeyBinding(KeyStroke keyStroke) {
        if (prohibited.contains(keyStroke)) {
            Static.log.error("Attempted to remove prohibited key binding: " + keyStroke);
            return;
        }
        String actionName = (String) inputMap.get(keyStroke);
        if (actionName != null) {
            inputMap.remove(keyStroke);
            actionMap.remove(actionName);
        }
    }

    /**
     * Adds a one-shot key binding that performs the given action and then removes itself.
     * @param keyStroke the keystroke to bind
     * @param actionName the name of the action to bind (used as the key in the action map)
     * @param action the action to perform when the keystroke is pressed
     */
    public KeyStroke addOneShotKeyBinding(KeyStroke keyStroke, String actionName, Action action) {
        Action oneShotAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
                removeKeyBinding(keyStroke);
            }
        };
        addKeyBinding(keyStroke, actionName, oneShotAction);

        return keyStroke;
    }

}
