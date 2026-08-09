package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.base.KeyedStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.commands.transform.handles.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.RotateMouseOwner;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * A sub-command of {@link TransformCmd} who uses the handles made by {@link AbstractTransform}
 * and the key-setters provided by {@link AbstractTransform} from {@link KeyedStatefulCommand}
 * to rotate a selection of points or triangles around a given axis.
 * <p>
 *     As like any other implementor of {@link AbstractTransform}, {@code rotate} provides the following
 *     functions based on the given input:
 *     <ul>
 *         <li>{@code (No Handle Selected) + UP/DOWN KEY} : {@code +/-(Z Axis)} </li>
 *         <li>{@code (No Handle Selected) + UP/DOWN KEY (Arbitrary Axis)} : {@code +/-(Y Axis)}</li>
 *         <li>{@code X Handle + UP/DOWN KEY} : {@code +/-(X Axis)}</li>
 *         <li>{@code Y Handle + UP/DOWN KEY} : {@code +/-(Y Axis)}</li>
 *         <li>{@code Z Handle + UP/DOWN KEY} : {@code +/-(Z Axis)}</li>
 *     </ul>
 * </p>
 * <p>
 *     Aliases: {@code rot}, {@code r}
 * </p>
 * <p>
 *     Unlike other {@link AbstractTransform} implementors, {@code rotate} provides a second (third when
 *     used) optional {@link TypedArg} which expects a {@link Vector3} for an arbitrary axis to rotate
 *     around.
 * </p>
 * <p>
 *     The mouse owner for rotate ({@link RotateMouseOwner}) actually provides a line indicating
 *     the actual axis you're about to rotate around (Only when a handle is selected. not including an
 *     arbitrary axis)
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     transform rotate             - Rotate Faces
 *     trans rot p                  - Rotate Points
 *     trans rot f (0, 0.2, 1.42)   - Rotate Faces around the Vector3 axis (0, 0.2, 1.42)
 *     selection r v (1, 0.3, 0.5)  - Rotate Points around the Vector3 axis (1, 0.3, 0.5)
 *     }</pre>
 * </p>
 * See {@link AbstractTransform} for more information onthe distinction between point mode and face mode.
 * @see TransformCmd
 * @see AbstractTransform
 * @see KeyedStatefulCommand
 * @see StatefulCommand
 * @see RotateMouseOwner
 * @see ArgSet
 * @see HandleType
 * @author Lehlogonolo Poole
 */
public class RotateSelection extends AbstractTransform {

    public static RotateMouseOwner rotateMouseOwner = new RotateMouseOwner();
    private Vector3 axis = new Vector3(true);

    RotateSelection() {
        super(
                "rotate", "Rotates the selection",
                "rotCmd", rotateMouseOwner,
                new double[]{45 / 2.0, 45, 90, 1});
        this.aliases("rot", "r").args(
                argSet,
                new TypedArg("arbitraryAxis", "An arbitrary axis to rotate around.", true, Vector3.class)
        ).parseUsages();

        Supplier<Vector3> supplier = () -> axis.isNotEmpty() ? axis :
                rotateMouseOwner.selectedHandle == null ? new Vector3(0, 1, 0) :
                        switch (rotateMouseOwner.selectedHandle.handleType()) {
                            case HandleType.X -> new Vector3(1, 0, 0);
                            case HandleType.Y -> new Vector3(0, 1, 0);
                            case HandleType.Z -> new Vector3(0, 0, 1);
                        };

        setUpKey(
                supplier,
                ignored -> false,
                axis -> references.forEach(gPoint -> rotateUpKey(axis, gPoint))
        );

        setDownKey(
                supplier,
                ignored -> false,
                axis -> references.forEach(gPoint -> rotateDownKey(axis, gPoint))
        );
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        axis = new Vector3(true);
        if (args.length > 1 && args[1] instanceof Vector3 a)
            axis = a.normalize();
        rotateMouseOwner.axis = axis;
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
    }

    public void rotateUpKey(Vector3 a, GPoint gPoint) {
        gPoint.setPivot(
                center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                a,
                                getCurrentStepSize()
                        )
                )
        );
    }

    public void rotateDownKey(Vector3 a, GPoint gPoint) {
        gPoint.setPivot(
                center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                a,
                                -getCurrentStepSize()
                        )
                )
        );
    }
}
