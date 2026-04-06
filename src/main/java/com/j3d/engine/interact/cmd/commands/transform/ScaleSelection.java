package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.handlers.ScaleMouseOwner;
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

public class ScaleSelection extends Subcommand  implements StatefulCommand<ArrayList<GPoint>> {

    private UUID overlapId;
    public static ScaleMouseOwner scaleMouseOwner = new ScaleMouseOwner();

    ScaleSelection() {
        super("scale", "Scales the selection");
        this.aliases("s", "size").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        CommandsManager.setAsCurrent(this);
        // previous current was the parent command and since a subcommand is called
        // by its parent and cant go through the CommandsParser.run() stack to be
        // classified as the current stateful, the stateful command if a subcommand
        // requires to classify itself. This is safe as its parent command was the current
        // running command.

        // Simple 3 dots
        ArrayList<GPoint> points = Static.renderer.getSelected()
                .stream()
                .filter(obj -> obj instanceof GPoint)
                .map(obj -> (GPoint) obj)
                .collect(Collectors.toCollection(ArrayList::new));

        run(this, "scaleCmd", points);
    }

    @Override
    public void onStart(ArrayList<GPoint> o) {
        scaleMouseOwner.requestOwnership();
        System.out.println("wuzup");
        Static.commandParser.toggleInputFieldDisabled();

        Vector3 sum = Vector3.reduceToVector3(
                o.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                , Vector3::add);
        Vector3 center = sum.div(o.size());
        double farPosX = Vector3.reduce(
                o.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getX() > v2)
                        return v1.getX();
                    return v2;
                },
                0d
        );
        double farPosY = Vector3.reduce(
                o.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getY() > v2)
                        return v1.getY();
                    return v2;
                },
                0d
        );
        double farPosZ = Vector3.reduce(
                o.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
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
            // this draws the handles such that the user can itneract with it
            // in real time and watch it warp and change.
            X.draw(g);
            Y.draw(g);
            Z.draw(g);
            g.setColor(Color.WHITE);

        };

        overlapId = UUID.randomUUID();

        Static.renderer.scheduleOverlap(overlapId, drawScaleHandle);
    }

    private void finished() {
        Static.renderer.removeOverlap(overlapId);
        Static.renderer.deselectAll();
        Static.mainFrame.repaint();
    }

    @Override
    public void onEnter(ActionEvent e, ArrayList<GPoint> o) {
        // later wrap as Action for the final ransform appled.
        EngineFrame.setMouseOwner(null);
        finished();
    }

    @Override
    public void onEsc(ActionEvent e, ArrayList<GPoint> o) {
        // clear transforms done.
        EngineFrame.setMouseOwner(null);
        finished();
    }
}
