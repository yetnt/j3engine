package com.j3d.gen.guide.steps;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.gen.guide.Anchor;
import com.j3d.gen.guide.GuidePanelAdapter;
import com.j3d.gen.guide.generic.DoubleClickStep;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ObjectExplainerStep extends DoubleClickStep {

    boolean built = false;

    public void setup() {

        if (built) return;
        built = true;

        // put a cube for the user to interact with.
        StaticRefs.getCommandParser().run(
                CommandsManager.commands.createCmd,
                new ArrayList<>(List.of("cube")),
                new ArrayList<>()
        );

        // get all GPoints
        ArrayList<GPoint> points =
                StaticRefs.getSceneManager().finder().find(
                                GPoint.class, Finder.allQuery(), null
                        ).stream()
                        .map(FindResult::getgObject)
                        // finder guarantees they are all GPoints
                        .map(GPoint.class::cast)
                        // sort by the closest to the camera
                        .sorted((p1, p2) -> {
                            Vector3 pos = p1.getPivot();
                            Vector3 pos2 = p2.getPivot();

                            Vector3 camPos = StaticRefs.getCamera().getPosition();

                            double d1 = camPos.distance(pos);
                            double d2 = camPos.distance(pos2);

                            return Double.compare(d1, d2);
                        })
                        // get top 4
                        .limit(4)
                        .collect(Collectors.toCollection(ArrayList::new));

        // pick a random point
        GPoint targetPoint = points.get(
                new Random().nextInt(points.size())
        );

        // set the colour to blue
        targetPoint.setColour(Color.BLUE);
    }

    @Override
    public void build(GuidePanelAdapter adapter) {
        setup();
        guideCounter(adapter);
        genericText(adapter, "(Double click to continue)");

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("(I've gone ahead and created a cube for you)")
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "Some objects like points are hidden by default. you can go (in the top right) "
                                                + "and click Scene > View > Wireframe (or use ALT+V) to change to a view where "
                                                + "all hidden stuff is visible."
                                )
                                .wrapDiv(400)
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "4"
                                )
                                .wrapHTML()
                ),
                Anchor.CENTRE,
                0, Anchor.offsetUp(150)
        );

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("The scene is comprised of many smaller geometry, referred to as just \"objects\"")
                                .addLn(
                                "These include points, lines, triangles and curves. All of these are types of objects."
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "These objects are then all stored within a single \"Thing\" (think of it as like a group of multiple"
                                ).add(
                                        " objects that make a single composite thing). Then furthermore multiple Things are stored within a single"
                                ).add(
                                        " \"Layer\""
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "(This is a later section but you can click in the above buttons (the toolbox) the Layer Tree to view all the Layers and Things)"
                                )
                                .wrapDiv(280)
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.EAST | Anchor.SOUTH,
                Anchor.offsetLeft(200), Anchor.offsetUp(380)
        );
    }
}
