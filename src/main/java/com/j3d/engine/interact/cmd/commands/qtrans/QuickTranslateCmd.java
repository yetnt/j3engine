package com.j3d.engine.interact.cmd.commands.qtrans;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.theme.CursorManager;
import com.j3d.ui.theme.CursorNames;

import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;


public class QuickTranslateCmd extends Subcommand implements StatefulCommand<Void> {
    /**
     * The mouse owner for {@link QuickTranslateCmd} to function
     */
    public static QTranslateMouseOwner qTranslateMouseOwner = new QTranslateMouseOwner();
    /**
     * The UUID which the orbit command uses to identify stuff registered by it. i.e. events or keybinds
     */
    public UUID qTransCmdUUID = UUID.randomUUID();

    ArrayList<Vector3> objectOriginalPosiions = new ArrayList<>();
    ArrayList<GPoint> pointsToTransform = new ArrayList<>();

    public QuickTranslateCmd() {
        super("qtrans", "Translate quickly.");
        this.aliases("quicktrans", "quick-translate", "move", "displace").parseUsages().addNoArgUsage();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (!CommandsManager.isCurrentStatefulRunning(this)) return;

        if (StaticRefs.getSceneManager().getSelected().isEmpty()) {
            logLabel.setText("No objects selected!");
            CommandsManager.clearCurrent();
            return;
        }

        StaticConfig.movementControls = false;
        StaticRefs.getSceneManager().getSelected().stream()
                .filter(obj -> obj instanceof GPoint)
                .map(o -> (GPoint)o)
                .forEach(o -> {
                    objectOriginalPosiions.add(o.getPivot());
                    pointsToTransform.add(o);
                });

        qTranslateMouseOwner.requestOwnership();
        qTranslateMouseOwner.using(pointsToTransform);
        run(this, "qTrans", null, logLabel);
    }

    @Override
    public void onStart(Void object, SafeJLabel label) {
        qTranslateMouseOwner.requestOwnership();
        CursorManager.set(CursorNames.HAND_GRABBING);
        StaticRefs.getMainPanel().repaint();
        StaticRefs.getSceneManager().scheduleOverlap(qTransCmdUUID, c ->
                {
                    Vector3 m = qTranslateMouseOwner.mouseLoc();
                    if (m != null) {
                        ScreenPoint sp = m.toPoint(StaticRefs.getCamera())
                                .toScreen();
                        // draw oval at sp
                        c.drawOval(
                                sp.x - GPoint.DIAMETER / 2,
                                sp.y - GPoint.DIAMETER / 2,
                                GPoint.DIAMETER,
                                GPoint.DIAMETER
                        );
                    }
                    label.setText("Move the objects to a new position (via the mouse)");
                }
        );

    }

    public void cleanup(SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        CursorManager.setDefault();
        StaticRefs.getSceneManager().removeOverlap(qTransCmdUUID);
        label.clear();
        CommandsManager.clearCurrent();
        objectOriginalPosiions.clear();
        pointsToTransform.clear();
        StaticRefs.getMainPanel().repaint();
        qTranslateMouseOwner.clear();
        StaticConfig.movementControls = true;
    }

    @Override
    public void onEnter(ActionEvent e, Void object, SafeJLabel label) {
        cleanup(label);
        VoidAction action = new VoidAction() {
            final ArrayList<GPoint> points = new ArrayList<>(pointsToTransform);
            final ArrayList<Vector3> original = new ArrayList<>(objectOriginalPosiions);
            final ArrayList<Vector3> newPositions = pointsToTransform.stream()
                    .map(GPoint::getPivot)
                    .collect(Collectors.toCollection(ArrayList::new));
            private final LocalTime now = LocalTime.now();
            @Override
            public Void run() {
                points.forEach(p -> p.setPivot(newPositions.get(points.indexOf(p))));
                return null;
            }

            @Override
            public void undo() {
                points.forEach(p -> p.setPivot(original.get(points.indexOf(p))));
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "quickTranslate:"+pointsToTransform.size();
            }

            @Override
            public LocalTime getTime() {
                return now;
            }
        };
        SceneManager.history.add(action);
        cleanup(label);
        // done
    }

    @Override
    public void onEsc(ActionEvent e, Void object, SafeJLabel label) {
        pointsToTransform.forEach(point -> point.setPivot(objectOriginalPosiions.get(pointsToTransform.indexOf(point))));
        cleanup(label);
    }

}
