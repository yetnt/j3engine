package com.j3d.engine.interact.cmd.commands.join;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.base.conditions.SelectionPreCondition;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class JoinCmd extends Command implements SemiStatefulCommand {

    LineJoin lineJoin = new LineJoin();
    private SelectionPreCondition selectionPreCondition;

    public JoinCmd() {
        super("join", "Join geometry");
        this.aliases("j").args(
                lineJoin
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1 || !(args[0] instanceof String s)) {
            // handle
            HashSet<GObject> objects = StaticRefs.getSceneManager().getSelected();
            if (objects.isEmpty()) {
                logLabel.setText("No objects selected");
                return;
            }
            handle(logLabel, new ArrayList<>(objects));
        } else {
            dispatchToSubcommands(s, logLabel, args, taggedArgs);
        }
    }

    public void handle(SafeJLabel label, ArrayList<GObject> objects) {
        // check if there's exactly 2 points. if so just call Line
        boolean allPoints = objects.stream().allMatch(
                o -> o instanceof GPoint
        );
        if (objects.size() == 2) {
            if (allPoints) {
                GPoint a = (GPoint) objects.getFirst();
                GPoint b = (GPoint) objects.getLast();

                lineJoin.run(
                        Invoker.byCommandCall(this),
                        label,
                        "line",
                        new Object[]{a, b},
                        new ArrayList<>()
                );
                return;
            }
        } else if (objects.size() == 3) {
            if (allPoints) {
                // ask the user to specify the control point.
                CommandsManager.setAsCurrent(this);

                StaticRefs.getSceneManager().getSelected().clear();

                selectionPreCondition = new SelectionPreCondition(
                        () -> {
                            CommandsManager.clearCurrent();
                            SelectionManager.selectionMouseOwner.clearSelectionSquare();
                            selectionPreCondition.finaliseCleanup();

                            // check the new selection if it ahs a single point
                            HashSet<GObject> newSelection = StaticRefs.getSceneManager().getSelected();
                            if (newSelection.size() != 1) {
                                label.setText(
                                        "Only a singular point can be selected!"
                                );
                                return;
                            }
                            if (!(new ArrayList<>(newSelection).getFirst() instanceof GPoint controlPoint)) {
                                label.setText(
                                        "Okay so like you need to select a POINT bro."
                                );
                                return;
                            }

                            handleCurve(label, new ArrayList<>(List.of(
                                    (GPoint)objects.getFirst(),
                                    (GPoint) objects.get(1),
                                    (GPoint) objects.getLast()
                            )), controlPoint);
                        },
                        () -> {
                            CommandsManager.clearCurrent();
                            SelectionManager.selectionMouseOwner.clearSelectionSquare();
                            SelectionManager.selectionMouseOwner.clearSelectionSquare();
                        },
                        "(Reselect a point to use as the control point for the curve)"
                );
                StaticRefs.getMainFrame().requestFocusInWindow(); // Remove focus from the command pallete

                if (selectionPreCondition.execute(label)) {
                    selectionPreCondition.finaliseCleanup();
                } else {
                    return;
                }
                return;
            }
        }

        // TODO: later do triangle creation via subcommand
        label.setText("I'm not sure how to join that... (select 2/3 points)");
    }

    public void handleCurve(SafeJLabel label, ArrayList<GPoint> alreadySelected, GPoint controlPoint) {
        ArrayList<GPoint> others = alreadySelected
                .stream().filter(
                        p -> !p.equals(controlPoint)
                ).collect(Collectors.toCollection(ArrayList::new));

        GPoint a = others.getFirst();
        GPoint b = others.getLast();

        lineJoin.run(
                Invoker.byCommandCall(this),
                label,
                "line",
                new Object[]{a, b, controlPoint},
                new ArrayList<>()
        );
    }
}
