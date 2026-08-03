package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 *  A command that dispatches to various measurement subcommands like {@link VolumeCmd} and {@link DistanceCmd}.
 * This command acts as a parent for all measurement-related operations.
 * <p>
 *     Aliases: {@code measure}, {@code meas}, {@code m}
 * </p>
 * @see VolumeCmd
 * @see DistanceCmd
 * @author Lehlogonolo Poole
 */
public class MeasureCmd extends Command {

    private VolumeCmd vol = new VolumeCmd();
    private DistanceCmd dist = new DistanceCmd();
    private AreaCmd area = new AreaCmd();

    public MeasureCmd() {
        super("measure", "Measures geometry");
        this.aliases("meas", "m")
                .args(vol, dist, area).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 1) {
            HashSet<GObject> selected = StaticRefs.getSceneManager().getSelected();
            if (selected.isEmpty()) {
                logLabel.setText("Select object(s) to infer a measurement on.");
                return;
            }
            infer(selected, logLabel);
            return;
        }
        String alias = (String) args[0];
        dispatchToSubcommands(alias, logLabel, args, taggedArgs);
    }

    public void infer(HashSet<GObject> selected, SafeJLabel logLabel) {
        // see if there's 2 points and a line
        if (selected.size() == 3 || selected.size() == 2) {
            // check that there's a single line in the list.
            if (selected.stream()
                    .filter(o -> o instanceof GLine)
                    .count() == 1) {
                // get the line
                GLine line = (GLine) selected.stream()
                        .filter(o -> o instanceof GLine)
                        .findFirst().orElse(null);
                assert line != null; // it wont be null anyway but java.
                dist.run(logLabel, "distance", new Object[]{
                        line.getA(),
                        line.getB()
                }, new ArrayList<>());
                return;
            }

            ArrayList<GPoint> points =
                    selected.stream()
                            .filter(o -> o instanceof GPoint)
                            .map(o -> (GPoint) o)
                            .collect(Collectors.toCollection(ArrayList::new));

            if (points.size() == 3) {
                // 3 points?

                area.run(logLabel, "area",
                        new Object[]{
                                points.get(0),
                                points.get(1),
                                points.get(2)
                        }, new ArrayList<>()
                );
                return;
            }

            // check that theres 2 points instead
            if (points.size() != 2)
                return;

            dist.run(logLabel, "distance", new Object[]{
                    points.get(0),
                    points.get(1)
            }, new ArrayList<>());

            return;

        }

        ArrayList<GTri> tris =
                selected.stream()
                        .filter(o -> o instanceof GTri)
                        .map(o -> (GTri) o)
                        .collect(Collectors.toCollection(ArrayList::new));

        if (tris.size() == 1) {
            GTri tri = tris.getFirst();
            area.run(logLabel, "area",
                    new Object[]{
                            tri.getWinding().first(),
                            tri.getWinding().second(),
                            tri.getWinding().third()
                    }, new ArrayList<>()
            );
            return;
        }
    }
}
