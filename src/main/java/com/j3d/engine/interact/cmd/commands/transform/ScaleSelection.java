package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.KeyedStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.commands.transform.handles.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.ScaleMouseOwner;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;

import java.util.function.Supplier;

/**
 * A sub-command of {@link TransformCmd} who uses the handles made by {@link AbstractTransform}
 * and the key-setters provided by {@link AbstractTransform} from {@link KeyedStatefulCommand}
 * to scale a selection of points or triangles.
 * <p>
 *     As like any other implementor of {@link AbstractTransform}, {@code scale} provides the following
 *     functions based on the given input:
 *     <ul>
 *         <li>{@code (No Handle Selected) + UP/DOWN KEY} : {@code +/-(Scale Uniformly)} </li>
 *         <li>{@code X Handle + UP/DOWN KEY} : {@code +/-(Scale X-Axis)}</li>
 *         <li>{@code Y Handle + UP/DOWN KEY} : {@code +/-(Scale Y-Axis)}</li>
 *         <li>{@code Z Handle + UP/DOWN KEY} : {@code +/-(Scale Z-Axis)}</li>
 *     </ul>
 * </p>
 * <p>
 *     Aliases: {@code s}, {@code size}, {@code sc}
 * </p>
 * <p>
 *     The mouse owner for translate ({@link ScaleMouseOwner}) provides no extra drawing or logic other than handle clicking
 *     which is inherited from {@link TransformMouseOwner}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     transform scale      - Scale Faces Uniformly
 *     trans sc p           - Scale Points Uniformly
 *     t size f             - Scale Faces Uniformly
 *     transform sc v       - Scale Points Uniformly
 *     sel scale t          - Scale Faces Uniformly
 *     }</pre>
 * </p>
 * See {@link AbstractTransform} for more information onthe distinction between point mode and face mode.
 * @see TransformCmd
 * @see AbstractTransform
 * @see KeyedStatefulCommand
 * @see StatefulCommand
 * @see ScaleMouseOwner
 * @see ArgSet
 * @see HandleType
 * @author Lehlogonolo Poole
 */
public class  ScaleSelection extends AbstractTransform {

    public static ScaleMouseOwner scaleMouseOwner = new ScaleMouseOwner();

    ScaleSelection() {
        super(
                "scale", "Scales the selection",
                "scaleCmd", scaleMouseOwner,
                // for scale since this doesn't scale up linearly, we define a set of multipliers/divisors
                new double[]{1.1, 1.3, 2, 1.01});
        this.aliases("s", "size", "sc").args(
                argSet
        ).parseUsages();

        Supplier<Vector3> scaleAxisCalculator = () -> scaleMouseOwner.selectedHandle == null ? new Vector3(true) :
                switch (scaleMouseOwner.selectedHandle.handleType()) {
                    case HandleType.X -> new Vector3(1, 0, 0);
                    case HandleType.Y -> new Vector3(0, 1, 0);
                    case HandleType.Z -> new Vector3(0, 0, 1);
                    case null -> new Vector3(true);
                };

        setUpKey(
                scaleAxisCalculator,
                ignored -> false,
                scaleAxis ->
                        references.stream().map(obj -> (GPoint) obj).forEach(
                                gpoint -> upKey(gpoint, scaleAxis)
                        )

        );

        setDownKey(
                scaleAxisCalculator,
                ignored -> false,
                scaleAxis ->
                        references.stream().map(obj -> (GPoint) obj).forEach(
                                gpoint -> downKey(gpoint, scaleAxis)
                        )

        );
    }

    public double getInverseStepSize() {
        // only allow 2 decimal places.
        return ((int)((1/getCurrentStepSize()) * 100)/100d);
    }

    public void upKey(GPoint gpoint, Vector3 scaleAxis) {
        if (scaleAxis.isNotEmpty()) {
            Vector3 fromCenter = gpoint.getPivot().sub(center);

            Vector3 scaled = new Vector3(
                    scaleAxis.getX() != 0 ? fromCenter.getX() * getCurrentStepSize() : fromCenter.getX(),
                    scaleAxis.getY() != 0 ? fromCenter.getY() * getCurrentStepSize() : fromCenter.getY(),
                    scaleAxis.getZ() != 0 ? fromCenter.getZ() * getCurrentStepSize() : fromCenter.getZ()
            );

            gpoint.setPivot(center.add(scaled));
        } else
            gpoint.setPivot(
                    center.add(gpoint.getPivot().sub(center).mult(getCurrentStepSize()))
            );
    }

    public void downKey(GPoint gpoint, Vector3 scaleAxis) {
        if (scaleAxis.isNotEmpty()) {
            Vector3 fromCenter = gpoint.getPivot().sub(center);

            Vector3 scaled = new Vector3(
                    scaleAxis.getX() != 0 ? fromCenter.getX() * getInverseStepSize() : fromCenter.getX(),
                    scaleAxis.getY() != 0 ? fromCenter.getY() * getInverseStepSize() : fromCenter.getY(),
                    scaleAxis.getZ() != 0 ? fromCenter.getZ() * getInverseStepSize() : fromCenter.getZ()
            );

            gpoint.setPivot(center.add(scaled));
        } else
            gpoint.setPivot(
                    center.add(gpoint.getPivot().sub(center).mult(getInverseStepSize()))
            );
    }
}
