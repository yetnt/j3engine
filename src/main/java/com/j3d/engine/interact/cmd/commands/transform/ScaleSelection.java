package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.handlers.ScaleMouseOwner;
import com.j3d.engine.interact.selection.SelectionManager;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ScaleSelection extends Subcommand {

    public static ScaleMouseOwner scaleMouseOwner = new ScaleMouseOwner();

    ScaleSelection() {
        super("scale", "Scales the selection");
        this.aliases("s", "size").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        scaleMouseOwner.requestOwnership();
        Static.commandParser.toggleInputFieldDisabled();
        // Simple 3 dots
        ArrayList<GPoint> points = Static.renderer.getSelected()
                .stream()
                .filter(obj -> obj instanceof GPoint)
                .map(obj -> (GPoint) obj)
                .collect(Collectors.toCollection(ArrayList::new));


        Vector3 sum = Vector3.reduceToVector3(
                points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                , Vector3::add);
        Vector3 center = sum.div(points.size());
        double farPosX = Vector3.reduce(
                points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getX() > v2)
                        return v1.getX();
                    return v2;
                },
                0d
        );
        double farPosY = Vector3.reduce(
                points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getY() > v2)
                        return v1.getY();
                    return v2;
                },
                0d
        );
        double farPosZ = Vector3.reduce(
                points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getZ() > v2)
                        return v1.getZ();
                    return v2;
                },
                0d
        );

        // Draw 3 circles
        // a blue one at x=0, y=4, z=0
        // a red one at x=4, y=0, z=0
        // a green one at x=0, y=0, z=4
        final int size = 10;
        Handle X = new Handle(
                HandleType.X, center.add(new Vector3(10, 0, 0)),
                (gr, p) -> {
                    gr.setColor(Color.BLUE);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Y = new Handle(
                HandleType.Y, center.add(new Vector3(0, 10, 0)),
                (gr, p) -> {
                    gr.setColor(Color.RED);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Z = new Handle(
                HandleType.Z, center.add(new Vector3(0, 0, 10)),
                (gr, p) -> {
                    gr.setColor(Color.GREEN);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });

        scaleMouseOwner.setHandles(new ArrayList<>(List.of(X, Y, Z)));
        Consumer<Graphics2D> drawScaleHandle = g -> {

            X.draw(g);
            Y.draw(g);
            Z.draw(g);
            g.setColor(Color.WHITE);

        };

        UUID handleIdSelect = UUID.randomUUID();

        Static.renderer.scheduleOverlap(handleIdSelect, drawScaleHandle);

        Static.keybinds.addOneShotKeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "confirmScale",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Static.renderer.removeOverlap(handleIdSelect);
                        SelectionManager.selectionMouseOwner.requestOwnership();
                        Static.commandParser.toggleInputFieldDisabled();
                        Static.renderer.deselectAll();
                        Static.mainFrame.repaint();
                    }
                }
        );
    }
}
