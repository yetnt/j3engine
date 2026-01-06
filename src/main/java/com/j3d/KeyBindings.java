package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandPallete;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

class KeyBindings {

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
                    Main.f.requestFocusInWindow();
                }
            }
        });

        // WASD and QE for camera movement

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveCameraForward");
        am.put("moveCameraForward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(0, 0, J3DSettings.cameraMoveSpeed));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveCameraBackward");
        am.put("moveCameraBackward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(0, 0, -J3DSettings.cameraMoveSpeed));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveCameraLeft");
        am.put("moveCameraLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(-J3DSettings.cameraMoveSpeed, 0, 0));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveCameraRight");
        am.put("moveCameraRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(J3DSettings.cameraMoveSpeed, 0, 0));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "moveCameraUp");
        am.put("moveCameraUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(0, J3DSettings.cameraMoveSpeed, 0));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "moveCameraDown");
        am.put("moveCameraDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.move(new Vector3(0, -J3DSettings.cameraMoveSpeed, 0));
                Main.f.repaint();
            }
        });

    // R to reset camera position (hold with shift for rotation reset) (hold shift and ctrl for both)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "resetCameraPosition");
        am.put("resetCameraPosition", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.setPosition(new Vector3(0, 0, 0));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK), "resetCameraRotation");
        am.put("resetCameraRotation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.setRotation(new Rotation(0, 0, 0));
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "resetCameraPositionAndRotation");
        am.put("resetCameraPositionAndRotation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.camera.setPosition(new Vector3(0, 0, 0));
                Main.camera.setRotation(new Rotation(0, 0, 0));
                Main.f.repaint();
            }
        });

        // Undo and Redo bindings

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoAction");
        am.put("undoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Renderer.history.undo();
                Main.f.repaint();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redoAction");
        am.put("redoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Renderer.history.redo();
                Main.f.repaint();
            }
        });
    }
}
