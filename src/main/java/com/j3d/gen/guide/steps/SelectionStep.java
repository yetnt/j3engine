package com.j3d.gen.guide.steps;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.react.events.payloads.SelectionEventPayload;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.gen.guide.Anchor;
import com.j3d.gen.guide.GuideInfo;
import com.j3d.gen.guide.GuidePanelAdapter;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SelectionStep extends GuideInfo {

    public SelectionStep() {
        super(
                SelectionManager.selectionMouseOwner
        );
    }

    public void setup() {

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

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("(I've gone ahead and created a cube for you)")
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "4"
                                )
                                .wrapHTML()
                ),
                Anchor.CENTRE,
                0, 200
        );

        guideCounter(adapter);
        genericText(adapter, "(Select ONLY the blue point to continue)");

        addCompAt(
                adapter,
                new JLabel("son"),
                Anchor.EAST | Anchor.CENTRE,
                50, 100
        );
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.X_SELECTED) {
            SelectionEventPayload payload
                    = (SelectionEventPayload) properties;

            // user must've selected a single object
            if (payload.getSelectedSet().size() != 1) return;

            GObject obj = (GObject) payload.getSelectedSet().toArray()[0];

            // it must be a point.
            if (!(obj instanceof GPoint point)) return;

            // it must be blue
            if (point.getColour() != Color.BLUE) return;

            // if its all true, then yippee
            close();
        }
    }
}
