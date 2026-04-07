package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.ScaleMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// TODO: Refine implementation.

public class ScaleSelection extends AbstractTransform {

    public static ScaleMouseOwner scaleMouseOwner = new ScaleMouseOwner();

    ScaleSelection() {
        super("scale", "Scales the selection", "scaleCmd", scaleMouseOwner);
        this.aliases("s", "size").args(
                argSet
        ).parseUsages();
        keys.add(new J3Key(
                        "scaleArrowUp",
                        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                double scale = 1.5;

                                Vector3 scaleAxis = scaleMouseOwner.selectedHandle == null ? new Vector3(true) :
                                switch (scaleMouseOwner.selectedHandle.handleType()) {
                                    case HandleType.X -> new Vector3(1, 0, 0);
                                    case HandleType.Y -> new Vector3(0, 1, 0);
                                    case HandleType.Z -> new Vector3(0, 0, 1);
                                    case null -> new Vector3(true);
                                };

                                references.stream().map(obj -> (GPoint) obj).forEach(
                                        gpoint -> {
                                            if (scaleAxis.isNotEmpty())
                                                gpoint.setPivot(
                                                        gpoint.getPivot().add(
                                                                scaleAxis.mult(scale)
                                                        )
                                                );
                                            else
                                                gpoint.setPivot(
                                                        center.add(gpoint.getPivot().sub(center).mult(scale))
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
