package com.j3d.gen.guide.steps;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.selection.SelectionUI;
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

/*
For this guide in particular we need to teach the user that they can interact with the scene by using selection

    -> Tell the user the view can be changed from wireframe and normal
    -> Tell the user about selection up vs selection down
        -> (Include U-selection and I-selection)
    -> Make the user play around with selection
 */

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
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "One useful thing to know is that you can go (in the top right) "
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
                0, 150
        );

        guideCounter(adapter);
        genericText(adapter, "(Select ONLY the blue point to continue)");

        JLabelRichText boldUnderline = new JLabelRichText()
                .underline().bold();

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(
                                "To select things, click and drag your mouse over what you want to select. "
                                + "There are different selection modes triggered from dragging up vs down (and keyboard input)"
                        )
                                .add(JLabelRichText.LINE_BREAK)
                                .addLn(
                                        "Dragging up shows a "
                                ).add(
                                        new JLabelRichText("green")
                                                .wrapUsing(boldUnderline).font(SelectionUI.STRICT_COLOR)
                                ).add(
                                        " selection square, which only selects everything that is fully inside it. (strict selection)"
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add("Dragging down shows a ")
                                .add(
                                        new JLabelRichText("yellow")
                                                .wrapUsing(boldUnderline).font(SelectionUI.SOFT_COLOR)
                                ).add(
                                        " selection square, which selects everything regardless. As long as its inside or "
                                        + "intersects with the square (soft selection)"
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "Additionally, you can "
                                )
                                .add(
                                        new JLabelRichText("add")
                                                .wrapUsing(boldUnderline).font(SelectionUI.UNION_COLOR)
                                )
                                .add(" to an existing selection by holding the ")
                                .add(
                                        new JLabelRichText("U").wrapUsing(boldUnderline).font(SelectionUI.UNION_COLOR)
                                )
                                .add(" key while dragging. and to "
                                ).add(
                                        new JLabelRichText("remove").wrapUsing(boldUnderline).font(SelectionUI.SUBTRACT_COLOR)
                                )
                                .add(" hold down the ")
                                .add(
                                        new JLabelRichText("I").wrapUsing(boldUnderline).font(SelectionUI.SUBTRACT_COLOR)
                                )
                                .add(
                                        " key instead. Although these 2 modes add/remove from the existing selection "
                                        + "they function similar to strict selecting.")
                                .wrapDiv(280)
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.EAST | Anchor.SOUTH,
                200, 380
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
