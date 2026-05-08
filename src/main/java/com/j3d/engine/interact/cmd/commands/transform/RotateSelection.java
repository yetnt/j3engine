package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.geometry.geo2d.BaseObject;
import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.RotateMouseOwner;

import java.util.ArrayList;
import java.util.function.Supplier;

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
                axis -> references.forEach(gPoint -> rotateUpKey(axis, gPoint)),
                (axis, m) ->
                        m.values().stream()
                                .filter(i -> i instanceof CPoint)
                                .map(i -> (CPoint)i)
                                .forEach(gPoint -> rotateUpKey(axis, gPoint))
        );

        setDownKey(
                supplier,
                ignored -> false,
                axis -> references.forEach(gPoint -> rotateDownKey(axis, gPoint)),
                (axis, m) ->
                        m.values().stream()
                                .filter(i -> i instanceof CPoint)
                                .map(i -> (CPoint)i)
                                .forEach(gPoint -> rotateDownKey(axis, gPoint))
        );
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        if (args.length > 1 && args[1] instanceof Vector3 a)
            axis = a.normalize();
        super.run(logLabel, aliasUsed, args, taggedArgs);
    }

    public void rotateUpKey(Vector3 a, BaseObject gPoint) {
        gPoint.setPivot(
                center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                a,
                                getCurrentStepSize()
                        )
                )
        );
    }

    public void rotateDownKey(Vector3 a, BaseObject gPoint) {
        gPoint.setPivot(
                center.add((gPoint.getPivot().sub(center)).rotateAroundAxis(
                                a,
                                -getCurrentStepSize()
                        )
                )
        );
    }
}
