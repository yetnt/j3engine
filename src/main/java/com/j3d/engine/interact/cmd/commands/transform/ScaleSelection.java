package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.BaseObject;
import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.ScaleMouseOwner;
import com.j3d.engine.interact.input.keyboard.J3Key;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO: Refine implementation.

public class ScaleSelection extends AbstractTransform {

    public static ScaleMouseOwner scaleMouseOwner = new ScaleMouseOwner();

    ScaleSelection() {
        super(
                "scale", "Scales the selection",
                "scaleCmd", scaleMouseOwner,
                // for scale since this doesnt scale up linearly, we define a set of multipliers/divisors
                new double[]{1.1, 1.3, 2, 1.01});
        this.aliases("s", "size").args(
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
                        ),
                (scaleAxis, m) ->
                        m.values().stream()
                                .filter(i -> i instanceof CPoint)
                                .map(i -> (CPoint)i).forEach(
                                gpoint -> upKey(gpoint, scaleAxis)
                        )

        );

        setDownKey(
                scaleAxisCalculator,
                ignored -> false,
                scaleAxis ->
                        references.stream().map(obj -> (GPoint) obj).forEach(
                                gpoint -> downKey(gpoint, scaleAxis)
                        ),
                (scaleAxis, m) ->
                        m.values().stream()
                                .filter(i -> i instanceof CPoint)
                                .map(i -> (CPoint)i).forEach(
                                        gpoint -> downKey(gpoint, scaleAxis)
                                )

        );
    }

    public double getInverseStepSize() {
        // only allow 2 decimal places.
        return ((int)((1/getCurrentStepSize()) * 100)/100d);
    }

    public void upKey(BaseObject gpoint, Vector3 scaleAxis) {
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

    public void downKey(BaseObject gpoint, Vector3 scaleAxis) {
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
