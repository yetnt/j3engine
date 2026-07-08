package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class VolumeCmd extends Subcommand {
    public VolumeCmd() {
        super("volume", "Measures he volume of a solid");
        this.aliases("v")
                .args(
                        new TypedArg("thing", "the solid to measure", false, Thing.class)
                ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1) {
            logLabel.setText("No arg given?");
            return;
        }
        if (args[0] instanceof Thing t) {
            if (!t.isSolid()) {
                logLabel.setText("This thing is not a solid.");
                return;
            }

            // pray and hope that this is a valid solid.

            double v = volume(t);

            logLabel.setText(v + " units cubed");
        }
    }

    private double volume(Thing t) {
        AtomicReference<Double> sum = new AtomicReference<>((double) 0);
        t.objectsStream()
                .filter(g -> g instanceof GTri)
                .map(g -> (GTri)g)
                .forEach( g -> {
                    sum.updateAndGet(v -> v + g.getWinding().first().getPivot().dot(
                            g.getWinding()
                                    .second()
                                    .getPivot()
                                    .cross(
                                            g.getWinding().third().getPivot()
                                    )
                    ));
                });
        double s = Math.abs(sum.get());

        return s / 6;
    }
}
