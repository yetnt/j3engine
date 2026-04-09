package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.TypedArg;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.RotateMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RotateSelection extends AbstractTransform {

    public static RotateMouseOwner rotateMouseOwner = new RotateMouseOwner();
    private Vector3 axis = new Vector3(true);

    RotateSelection() {
        super(
                "rotate", "Rotates the selection",
                "rotCmd", rotateMouseOwner,
                new double[]{45 / 2.0, 45, 90, 1});
        this.aliases("rot", "r").args(
                argSet,
                new TypedArg("arbitraryAxis", "An arbitrary axis to rotate around.", true, Vector3.class)
        ).parseUsages();

        keys.add(
                // Right key
                new J3Key(
                        "rotateRight",
                        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (axis.isNotEmpty()) return;
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );

        keys.add(
                // Left key
                new J3Key(
                        "rotateLeft",
                        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (axis.isNotEmpty()) return;
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );

        keys.add(
                // Up key
                new J3Key(
                        "rotateUp",
                        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Vector3 a =
                                        axis.isNotEmpty() ? axis :
                                                rotateMouseOwner.selectedHandle == null ? new Vector3(0, 1, 0) :
                                        switch (rotateMouseOwner.selectedHandle.handleType()) {
                                            case HandleType.X -> new Vector3(1, 0, 0);
                                            case HandleType.Y -> new Vector3(0, 1, 0);
                                            case HandleType.Z -> new Vector3(0, 0, 1);
                                        };
                                references.forEach(
                                        gPoint -> {
                                            gPoint.setPivot(
                                                    center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                                            a,
                                                            getCurrentStepSize()
                                                    )
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
                // Down key
                new J3Key(
                        "rotateDown",
                        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Vector3 a =
                                        axis.isNotEmpty() ? axis :
                                                rotateMouseOwner.selectedHandle == null ? new Vector3(0, 1, 0) :
                                                        switch (rotateMouseOwner.selectedHandle.handleType()) {
                                                            case HandleType.X -> new Vector3(1, 0, 0);
                                                            case HandleType.Y -> new Vector3(0, 1, 0);
                                                            case HandleType.Z -> new Vector3(0, 0, 1);
                                                        };
                                references.forEach(
                                        gPoint -> {
                                            gPoint.setPivot(
                                                    center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                                            a,
                                                            -getCurrentStepSize()
                                                    ))
                                            );
                                        }
                                );
                                Static.mainPanel.repaint();
                            }
                        }
                )
        );
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length > 1 && args[1] instanceof Vector3 a)
            axis = a.normalize();
        super.run(logLabel, aliasUsed, args, taggedArgs);
    }
}
