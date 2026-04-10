package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.handlers.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TranslateMouseOwner;

import javax.swing.*;

public class TranslateSelection extends AbstractTransform {

    public static TranslateMouseOwner translateMouseOwner = new TranslateMouseOwner();

    TranslateSelection() {
        super(
                "translate", "Translates the selection",
                "translateCmd", translateMouseOwner,
                new double[]{1, 5, 20, 0.1});
        this.aliases("t", "trans","move","mv", "m").args(
                argSet
        ).parseUsages();

        // this arrow is only functional when no handle is selected
        setLeftKey(
                () -> null,
                (o) -> translateMouseOwner.selectedHandle != null,
                (o) -> references.forEach(
                        gpoint ->
                                gpoint.setPivot(
                                        gpoint.getPivot().sub(new Vector3(getCurrentStepSize(), 0, 0))
                                )
                ), (o, m) ->
                        m.values().stream()
                                .filter(i -> i instanceof CPoint)
                                .map(i -> (CPoint)i)
                                .forEach(
                                        gpoint -> gpoint.setPivot(
                                                        gpoint.getPivot().sub(new Vector3(getCurrentStepSize(), 0, 0))
                                        )
                ));


        // this arrow is only functional when no handle is selected
        setRightKey(
                () -> null,
                (o) -> translateMouseOwner.selectedHandle != null,
                (o) -> references.forEach(
                        gpoint ->
                                gpoint.setPivot(
                                        gpoint.getPivot().add(new Vector3(getCurrentStepSize(), 0, 0))
                                )
                ), (o, m) -> m.values().stream()
                        .filter(i -> i instanceof CPoint)
                        .map(i -> (CPoint)i)
                        .forEach(
                                gpoint ->
                                        gpoint.setPivot(
                                                gpoint.getPivot().add(new Vector3(getCurrentStepSize(), 0, 0))
                                        )
                        ));

        setUpKey(
                () -> null,
                (o) -> false,
                (o) -> references.forEach(
                        gpoint -> {
                            if (translateMouseOwner.selectedHandle == null) {
                                gpoint.setPivot(
                                        gpoint.getPivot().add(new Vector3(0, 0, getCurrentStepSize()))
                                );
                                return;
                            }

                            gpoint.setPivot(
                                    gpoint.getPivot().add(
                                            switch (translateMouseOwner.selectedHandle.handleType()) {
                                                case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                            }
                                    )
                            );
                        }
                ),
                (o, m) -> m.values().stream()
                        .filter(i -> i instanceof CPoint)
                        .map(i -> (CPoint)i).forEach(
                            (gpoint) -> {
                                if (translateMouseOwner.selectedHandle == null) {
                                    gpoint.setPivot(
                                            gpoint.getPivot().add(new Vector3(0, 0, getCurrentStepSize()))
                                    );
                                    return;
                                }

                                gpoint.setPivot(
                                        gpoint.getPivot().add(
                                                switch (translateMouseOwner.selectedHandle.handleType()) {
                                                    case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                    case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                    case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                                }
                                        )
                                );
                            }
                        )
        );

        setDownKey(
                () -> null,
                (o) -> false,
                (o) -> references.forEach(
                        gpoint -> {
                            if (translateMouseOwner.selectedHandle == null) {
                                gpoint.setPivot(
                                        gpoint.getPivot().sub(new Vector3(0, 0, getCurrentStepSize()))
                                );
                                return;
                            }

                            gpoint.setPivot(
                                    gpoint.getPivot().sub(
                                            switch (translateMouseOwner.selectedHandle.handleType()) {
                                                case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                            }
                                    )
                            );
                        }
                ),
                (o, m) -> m.values().stream()
                        .filter(i -> i instanceof CPoint)
                        .map(i -> (CPoint)i).forEach(
                                (gpoint) -> {
                                    if (translateMouseOwner.selectedHandle == null) {
                                        gpoint.setPivot(
                                                gpoint.getPivot().sub(new Vector3(0, 0, getCurrentStepSize()))
                                        );
                                        return;
                                    }

                                    gpoint.setPivot(
                                            gpoint.getPivot().sub(
                                                    switch (translateMouseOwner.selectedHandle.handleType()) {
                                                        case HandleType.X -> new Vector3(getCurrentStepSize(), 0, 0);
                                                        case HandleType.Y -> new Vector3(0, getCurrentStepSize(), 0);
                                                        case HandleType.Z -> new Vector3(0, 0, getCurrentStepSize());
                                                    }
                                            )
                                    );
                                }
                        )
        );
    }
}
