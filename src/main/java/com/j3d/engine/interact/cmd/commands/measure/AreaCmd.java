package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.threads.BlinkerSwingWorker;
import com.j3d.ui.SafeJLabel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.j3d.StaticRefs.getCamera;
import static com.j3d.StaticRefs.getSceneManager;

/**
 *  A subcommand for measuring the area of a triangle defined by three points.
 *  <p>
 *      Aliases: {@code area}, {@code a}
 *  </p>
 * @author Lehlogonolo Poole
 * @see VolumeCmd
 * @see DistanceCmd
 */
public class AreaCmd extends Subcommand {
    public AreaCmd() {
        super("area", "Measure area");
        this.aliases("a").args(
                new TypedArg("a", "point A", false, GPoint.class, Vector3.class),
                new TypedArg("b", "point B", false, GPoint.class, Vector3.class),
                new TypedArg("c" , "point C", false, GPoint.class, Vector3.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length < 3) {
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
        Vector3 C = switch (args[2]) {
            case GPoint p -> p.getPivot();
            case Vector3 v -> v;
            default ->  null;
        };
        if (A == null || B == null || C == null) {
            logLabel.setText("One of the inputs was not given a type of vector3");
            return;
        }

        double area = Triangle.area(A, B, C);
        logLabel.setText(area + " units^2");
        ghost(A, B, C, area);
    }

    private boolean draw = true;

    private void ghost(Vector3 A, Vector3 B, Vector3 C, double area) {
        UUID id = UUID.randomUUID();
        getSceneManager().scheduleOverlap(id, (c) -> {
            if (!draw) return;
            c.setColor(new Color(206, 0, 0));
            c.setStroke(new BasicStroke(1.8f));
            getSceneManager().drawPoly3D(
                    c, new ArrayList<>(List.of(A, B, C))
            );
            getSceneManager().drawText3D(
                    c, A.add(B).div(2), String.format("%.2f units^2", area),
                    getCamera()
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
