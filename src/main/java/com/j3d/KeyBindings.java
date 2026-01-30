package com.j3d;

import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.ui.home.EngineFrame;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandPallete;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class KeyBindings {

    public KeyBindings(InputMap im, ActionMap am, CommandPallete cmdP) {
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputEvent.SHIFT_DOWN_MASK), "focusCommandPallete");
        am.put("focusCommandPallete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdP.inputField.requestFocusInWindow();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "defocusCommandPallete");
        am.put("defocusCommandPallete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmdP.inputField.isFocusOwner()) {
                    EngineFrame.f.requestFocusInWindow();
                }
            }
        });

        // WASD and QE for camera movement

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveCameraForward");
        am.put("moveCameraForward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(0, 0, J3DSettings.cameraMoveSpeed));
                EngineFrame.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveCameraBackward");
        am.put("moveCameraBackward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(0, 0, -J3DSettings.cameraMoveSpeed));
                EngineFrame.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveCameraLeft");
        am.put("moveCameraLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(-J3DSettings.cameraMoveSpeed, 0, 0));
                EngineFrame.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveCameraRight");
        am.put("moveCameraRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(J3DSettings.cameraMoveSpeed, 0, 0));
                EngineFrame.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "moveCameraUp");
        am.put("moveCameraUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(0, J3DSettings.cameraMoveSpeed, 0));
                EngineFrame.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "moveCameraDown");
        am.put("moveCameraDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EngineFrame.camera.move(new Vector3(0, -J3DSettings.cameraMoveSpeed, 0));
                EngineFrame.f.repaint();
            }
        });


        // I selection

        AbstractAction clearInferredSelectionType = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.NONE;
            }
        };

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false), "selectSubtract");
        am.put("selectSubtract", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.SUBTRACT;
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, true), "selectSubtractUp");
        am.put("selectSubtractUp", clearInferredSelectionType);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK, false), "selectAdd");
        am.put("selectAdd", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectionUI.inferredSelection = SelectionUtils.InferredSelectionType.ADD;
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK, true), "selectAddUp");
        am.put("selectAddUp", clearInferredSelectionType);
    }
}
