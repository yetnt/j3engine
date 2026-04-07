package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.base.Subcommand;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.engine.EngineFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AbstractTransform extends Subcommand implements StatefulCommand<Void> {

    protected UUID overlapId;
    private final TransformMouseOwner mouseOwner;
    protected ArrayList<J3Key> keys = new ArrayList<>();
    protected ArrayList<Vector3> originalPointPos = new ArrayList<>();
    protected ArrayList<GPoint> references = new ArrayList<>();
    protected Vector3 center;
    protected ArgSet argSet =
            new ArgSet(
                    "mode",
                    "What the transformation should operate on",
                    true,
                    "p", "v", // Points/vertices
                    "t", "f" // Triangles/faces
            );
    protected String eventName;

    public AbstractTransform(String commmandName, String commandDesc, String eventName, TransformMouseOwner mouseOwner) {
        super(commmandName, commandDesc);
        this.mouseOwner = mouseOwner;
        this.eventName = eventName;
    }


    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        CommandsManager.setAsCurrent(this);

        boolean faceMode = true;
        if (args.length > 0 && !(args[0] instanceof String)) {
            Static.log.println("Second argument has to be a string!");
            return;
        }
        if (args.length > 0 && argSet.isValid((String)args[0])) {
            String arg = (String)args[0];
            faceMode = arg.equals("f") || arg.equals("v");
        }

        // Simple 3 dots
        references =
                faceMode ?
                        new ArrayList<>(Static.renderer.getSelected().stream()
                                .filter(obj -> obj instanceof GTri)
                                .map(obj -> (GTri) obj)
                                .flatMap(GTri::getLegStream)
                                .flatMap(GLine::getPointStream)
                                .collect(Collectors.toSet()))
                        : Static.renderer.getSelected()
                        .stream()
                        .filter(obj -> obj instanceof GPoint)
                        .map(obj -> (GPoint) obj)
                        .collect(Collectors.toCollection(ArrayList::new));

        originalPointPos = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        run(this, eventName, null);
    }

    @Override
    public void onStart(Void o) {
        mouseOwner.requestOwnership();
        System.out.println("wuzup");
        Static.commandParser.toggleInputFieldDisabled();

        keys.forEach(
                key -> Static.keybinds.registerJ3Key(key)
        );

        center = Vector3.reduceToVector3(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                , Vector3::add).div(references.size());
        double farPosX = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getX() > v2)
                        return v1.getX();
                    return v2;
                },
                0d
        );
        double farPosY = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
                (v1, v2) -> {
                    if (v1.getY() > v2)
                        return v1.getY();
                    return v2;
                },
                0d
        );
        double farPosZ = Vector3.reduce(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)),
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
                    gr.setColor(Color.RED);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Y = new Handle(
                HandleType.Y, center.add(new Vector3(0, 10, 0)),
                (gr, p) -> {
                    gr.setColor(Color.BLUE);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });
        Handle Z = new Handle(
                HandleType.Z, center.add(new Vector3(0, 0, 10)),
                (gr, p) -> {
                    gr.setColor(Color.GREEN);
                    gr.fillOval(p.x - size / 2, p.y - size / 2, size, size);
                });

        mouseOwner.setHandles(new ArrayList<>(List.of(X, Y, Z)), references);
        Consumer<Graphics2D> drawScaleHandle = g -> {
            // this draws the handles such that the user can itneract with it
            // in real time and watch it warp and change.
            center = Vector3.reduceToVector3(
                    references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                    , Vector3::add).div(references.size());
            X.setPos(center.add(new Vector3(10, 0, 0)));
            Y.setPos(center.add(new Vector3(0, 10, 0)));
            Z.setPos(center.add(new Vector3(0, 0, 10)));
            X.draw(g);
            Y.draw(g);
            Z.draw(g);
            g.setColor(Color.WHITE);

        };

        overlapId = UUID.randomUUID();

        Static.renderer.scheduleOverlap(overlapId, drawScaleHandle);
    }

    private void finished() {
        keys.forEach(
                key -> Static.keybinds.removeJ3Key(key.getId())
        );
        Static.renderer.removeOverlap(overlapId);
        Static.renderer.deselectAll();
        Static.mainFrame.repaint();
    }


    @Override
    public void onEnter(ActionEvent e, Void o) {
        // later wrap as Action for the final ransform appled.
        EngineFrame.setMouseOwner(null);
        ArrayList<Vector3> newPositions = references.stream().map(GObject::getPivot).collect(Collectors.toCollection(ArrayList::new));
        Renderer.history.add(
                new VoidAction() {
                    @Override
                    public Void run() {
                        references.forEach(p -> p.setPivot(newPositions.get(references.indexOf(p))));
                        return null;
                    }

                    @Override
                    public void undo() {
                        references.forEach(p -> p.setPivot(originalPointPos.get(references.indexOf(p))));
                    }

                    @Override
                    public boolean isReversible() {
                        return true;
                    }

                    @Override
                    public String getDescription() {
                        return "TransformSelection:"+getName();
                    }
                }
        );
        finished();
    }

    @Override
    public void onEsc(ActionEvent e, Void o) {
        // clear transforms done.
        EngineFrame.setMouseOwner(null);
        for (GPoint p : references) p.setPivot(originalPointPos.get(references.indexOf(p)));
        finished();
    }
}
