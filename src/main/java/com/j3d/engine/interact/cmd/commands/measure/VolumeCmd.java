package com.j3d.engine.interact.cmd.commands.measure;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.threads.BlinkerSwingWorker;
import com.j3d.ui.SafeJLabel;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.j3d.StaticRefs.*;

/**
 *  * A subcommand of {@link MeasureCmd} that calculates and displays the volume of a solid
 *  {@link Thing}.
 * <p>
 *     Aliases: {@code volume}, {@code v}
 * </p>
 * Typical Usages:
 * <pre>{@code
 *     measure volume "mySolidObject" - Measures the volume of the thing named "mySolidObject".
 *     m v 08415ef7-770b-4367-83d6-2dc0c64914b1 - Measures the volume of the thing with the given UUID.
 *     }</pre>
 * <p>
 *     This command also makes use of {@link BlinkerSwingWorker} to highlight the solid being
 *     measured.
 * </p>
 * @author Lehlogonolo Poole
 */
public class VolumeCmd extends Subcommand {
    public VolumeCmd() {
        super("volume", "Measures he volume of a solid");
        this.aliases("v")
                .args(
                        new TypedArg("thing", "the solid to measure", false, Thing.class)
                ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
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

            registerGhost(t, v);
        }
    }

    private boolean draw = true;

    private void registerGhost(Thing t, double volume) {
        UUID id = UUID.randomUUID();

        getSceneManager().scheduleOverlap(id, (c) -> {
            ghost(t, c);
            Vector3 v = closestPoint(t).getPivot();
            getSceneManager().drawText3D(
                    c, v, String.format("%.2f units cubed", volume),
                    getCamera()
            );
        });

        // schedule swing worker to remove thread after its slept for 5 seconds
        double seconds = 0.5;
        BlinkerSwingWorker w = new BlinkerSwingWorker(
                (long) (seconds*1e3), 10, () -> draw = !draw,
                id
        );
        w.execute();
    }

    private void ghost(Thing t, Graphics2D graphics2D) {
        graphics2D.setColor(Color.WHITE);
        t.objectsStream()
                .filter(o -> o instanceof GLine)
                .map(o -> (GLine) o)
                .forEach(g -> {
                    if (draw)
                        g.toSegment().swingDraw(graphics2D);
                });
    }

    private GPoint closestPoint(Thing t) {
        return t.objectsStream()
                .filter(o -> o instanceof GPoint)
                .map(o -> (GPoint) o).min((a, b) -> {
                    Vector3 cameraPos = StaticRefs.getCamera().getPosition();
                    double distA = a.getPivot().distance(cameraPos);
                    double distB = b.getPivot().distance(cameraPos);
                    return Double.compare(distA, distB);
                })
                .orElse(null);
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
