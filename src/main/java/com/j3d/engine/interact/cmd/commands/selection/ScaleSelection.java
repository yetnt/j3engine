package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.ui.engine.EngineFrame;

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
        Consumer<Graphics2D> drawScaleHandle = g -> {
            // Simple 3 dots
            ArrayList<GPoint> points = Static.renderer.getSelected()
                    .stream()
                    .filter(obj -> obj instanceof GPoint)
                    .map(obj -> (GPoint) obj)
                    .collect(Collectors.toCollection(ArrayList::new));


            Vector3 sum = Vector3.reduce(
                    points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                    , Vector3::add);
            Vector3 center = sum.div(points.size());
            Vector3 farPosX = Vector3.reduce(
                    points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                    (v1, v2) -> {
                        if (v1.getX() > v2.getX()) {
                            return v1;
                        }
                        return v2;
                    }
            );
            double farDoubleX = center.getX() - farPosX.getX();
            Vector3 farPosY = Vector3.reduce(
                    points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                    (v1, v2) -> {
                        if (v1.getY() > v2.getY()) {
                            return v1;
                        }
                        return v2;
                    }
            );
            double farDoubleY = center.getY() - farPosY.getY();
            Vector3 farPosZ = Vector3.reduce(
                    points.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                    (v1, v2) -> {
                        if (v1.getZ() > v2.getZ()) {
                            return v1;
                        }
                        return v2;
                    }
            );
            double farDoubleZ = center.getZ() - farPosZ.getZ();

            // Draw 3 circles
            // a blue one at x=0, y=4, z=0
            // a red one at x=4, y=0, z=0
            // a green one at x=0, y=0, z=4
            ScreenPoint red = center.add(new Vector3(10, 0, 0)).toPoint(Static.camera).toScreen(Static.renderer);
            ScreenPoint blue = center.add(new Vector3(0, 10, 0)).toPoint(Static.camera).toScreen(Static.renderer);
            ScreenPoint green = center.add(new Vector3(0, 0, 10)).toPoint(Static.camera).toScreen(Static.renderer);
            scaleMouseOwner.setHandlePositions(new ArrayList<>(List.of(red, blue, green)));
//            ScreenPoint blue = center.add(new Vector3(0, farDoubleY, 0)).toPoint(Static.camera).toScreen(Static.renderer);
//            ScreenPoint red = center.add(new Vector3(farDoubleX, 0, 0)).toPoint(Static.camera).toScreen(Static.renderer);
//            ScreenPoint green = center.add(new Vector3(0, 0, farDoubleZ)).toPoint(Static.camera).toScreen(Static.renderer);
            int size = 10;
            g.setColor(Color.BLUE);
            g.fillOval(blue.x - size / 2, blue.y - size / 2, size, size);
            g.setColor(Color.RED);
            g.fillOval(red.x - size / 2, red.y - size / 2, size, size);
            g.setColor(Color.GREEN);
            g.fillOval(green.x - size / 2, green.y - size / 2, size, size);
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
                    }
                }
        );
    }
}
