package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.threads.BlinkerSwingWorker;
import com.j3d.ui.SafeJLabel;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static com.j3d.StaticRefs.camera;
import static com.j3d.StaticRefs.sceneManager;

/**
 * A subcommand of {@link MeasureCmd} that calculates and displays the distance between two points.
 * <p>
 *     Aliases: {@code distance}, {@code d}, {@code dist}
 * </p>
 * Typical Usages:
 * <pre>{@code
 *     measure distance (10, 20, 30) (40, 50, 60) - Measures the distance between two Vector3 points.
 *     m d 08415ef7-770b-4367-83d6-2dc0c64914b1 ... - Measures the distance between two GPoint objects
 *     }</pre>
 * <p>
 *     This command also makes use of {@link BlinkerSwingWorker} such as to draw the line
 *     between the 2 {@link Vector3} points.
 * </p>
 * @author Lehlogonolo Poole
 */
public class DistanceCmd extends Subcommand {
    public DistanceCmd() {
        super("distance", "Measure distance");
        this.aliases("d", "dist", "length", "l").args(
                new TypedArg("a", "point A", false, GPoint.class, Vector3.class),
                new TypedArg("b", "point B", false, GPoint.class, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 2) {
            logLabel.setText("No arg given?");
            return;
        }
        Vector3 A = switch (args[0]) {
            case GPoint p -> p.getPivot();
            case Vector3 v -> v;
            default ->  null;
        };
        Vector3 B = switch (args[1]) {
            case GPoint p -> p.getPivot();
            case Vector3 v -> v;
            default ->  null;
        };
        if (A == null || B == null) {
            logLabel.setText("One of the inputs was not given a type of vector3");
            return;
        }

        double d = A.distance(B);
        logLabel.setText(d + " units long");
        ghost(A, B, d);
    }

    private boolean draw = true;

    private void ghost(Vector3 A, Vector3 B, double d) {
        UUID id = UUID.randomUUID();
        sceneManager.scheduleOverlap(id, (c) -> {
            if (!draw) return;
            c.setColor(new Color(206, 0, 0));
            c.setStroke(new BasicStroke(1.8f));
            sceneManager.drawLine3D(
                    c, A, B, camera
            );
            sceneManager.drawText3D(
                    c, A.add(B).div(2), String.format("%.2f units long", d),
                    camera
            );
        });
        // schedule swing worker to remove thread after its slept for 5 seconds
        double seconds = 0.5;
        BlinkerSwingWorker blinkerSwingWorker = new BlinkerSwingWorker(
                (long) (seconds*1e3), 20, () -> draw = !draw,
                id
        );
        blinkerSwingWorker.execute();
    }
}
