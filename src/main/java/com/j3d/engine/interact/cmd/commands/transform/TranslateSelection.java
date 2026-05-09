package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.base.KeyedStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.commands.transform.handles.HandleType;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TranslateMouseOwner;

/**
 * A sub-command of {@link TransformCmd} who uses the handles made by {@link AbstractTransform}
 * and the key-setters provided by {@link AbstractTransform} from {@link KeyedStatefulCommand}
 * to translate a selection of points or triangles.
 * <p>
 *     As like any other implementor of {@link AbstractTransform}, {@code translate} provides the following
 *     functions based on the given input:
 *     <ul>
 *         <li>{@code (No Handle Selected) + UP/DOWN KEY} : {@code +/-Z} </li>
 *         <li>{@code (No Handle Selected) + LEFT/RIGHT KEY} : {@code +/-X}</li>
 *         <li>{@code X Handle + UP/DOWN KEY} : {@code +/-X}</li>
 *         <li>{@code Y Handle + UP/DOWN KEY} : {@code +/-Y}</li>
 *         <li>{@code Z Handle + UP/DOWN KEY} : {@code +/-Z}</li>
 *     </ul>
 * </p>
 * <p>
 *     Aliases: {@code t}, {@code trans}, {@code move}, {@code mv}, {@code m}
 * </p>
 * <p>
 *     The mouse owner for translate ({@link TranslateMouseOwner}) provides no extra drawing or logic other than handle clicking
 *     which is inherited from {@link TransformMouseOwner}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     transform t      - Translate selection in face mode
 *     transform move f - Translate selection in face mode
 *     transform mv t   - Translate selection in face mode
 *     t t v            - Translate selection in point mode
 *     t mv p           - Translate seleciton in point mode
 *     trans trans      - Translate selection in face mode
 *     }</pre>
 * </p>
 * See {@link AbstractTransform} for more information onthe distinction between point mode and face mode.
 * @see TransformCmd
 * @see AbstractTransform
 * @see KeyedStatefulCommand
 * @see StatefulCommand
 * @see TranslateMouseOwner
 * @see ArgSet
 * @see HandleType
 * @author Lehlogonolo Poole
 */
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
