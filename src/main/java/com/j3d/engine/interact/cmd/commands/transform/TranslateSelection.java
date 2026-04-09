package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TranslateMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class TranslateSelection extends AbstractTransform {

    public static TranslateMouseOwner translateMouseOwner = new TranslateMouseOwner();

    TranslateSelection() {
        super(
                "translate", "Translates the selection",
                "translateCmd", translateMouseOwner,
                new double[]{1, 5, 20, 0.1});
        this.aliases("t", "trans","move","mv", "m").args(
                argSet
        ).parseUsages();

        keys.add(
                // Left Arrow
                new J3Key(
                        "translateArrowLeft",
                        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // this arrow is only functional when no handle is selected
                                if (translateMouseOwner.selectedHandle != null) return;
                                references.forEach(
                                        gpoint ->
                                            gpoint.setPivot(
                                                    gpoint.getPivot().sub(new Vector3(getCurrentStepSize(), 0, 0))
                                            )
                                );
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );

        keys.add(
                // Right Arrow
                new J3Key(
                        "translateArrowRight",
                        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // this arrow is only functional when no handle is selected
                                if (translateMouseOwner.selectedHandle != null) return;
                                references.forEach(
                                        gpoint ->
                                                gpoint.setPivot(
                                                        gpoint.getPivot().add(new Vector3(getCurrentStepSize(), 0, 0))
                                                )
                                );
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );

        keys.add(
                // Up Arrow
                new J3Key(
                        "translateArrowUp",
                        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                references.forEach(
                                        gpoint -> {
                                            if (translateMouseOwner.selectedHandle == null) {
                                                gpoint.setPivot(
                                                        gpoint.getPivot().add(new Vector3(0, 0, getCurrentStepSize()))
                                                );
                                                return;
                                            }

                                            gpoint.setPivot(
                                                    gpoint.getPivot().add(
                                                            switch (translateMouseOwner.selectedHandle.handleType()) {
                                                                case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                                case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                                case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                                            }
                                                    )
                                            );
                                        }
                                );
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );

        keys.add(
                // Down Arrow
                new J3Key(
                        "translateArrowDown",
                        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                references.forEach(
                                        gpoint -> {
                                            if (translateMouseOwner.selectedHandle == null) {
                                                gpoint.setPivot(
                                                        gpoint.getPivot().sub(new Vector3(0, 0, getCurrentStepSize()))
                                                );
                                                return;
                                            }

                                            gpoint.setPivot(
                                                    gpoint.getPivot().sub(
                                                            switch (translateMouseOwner.selectedHandle.handleType()) {
                                                                case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                                case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                                case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                                            }
                                                    )
                                            );
                                        }
                                );
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );
    }
}
