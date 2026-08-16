package com.j3d.gen.guide.steps;

import com.j3d.engine.interact.input.MouseClickPayload;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.guide.*;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class WelcomeStep extends GuideInfo {

    public WelcomeStep() {
        super(
                AlwaysMouseOwner.getSingleInstance()
        );
    }

    @Override
    public void build(GuidePanelAdapter adapter) {
        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("Welcome to ")
                                .wrapUsing(adapter.readableTextStyle)
                                .add(
                                        new JLabelRichText("J3Engine").italic()
                                ).wrapHTML()
                ),
                Anchor.CENTRE,
                0, 220
        );

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("This tutorial/guide will help you with getting started with J3Engine.")

                                .wrapDiv(200).font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.CENTRE,
                0, 120
        );

        genericText(adapter, "(Double click to continue)");

        addCompAt(
                adapter,
                image(
                        Objects.requireNonNull(WelcomeStep.class.getResource("/art/logo/J3Engine.png")),
                        0.07
                ),
                Anchor.CENTRE,
                0, -200
        );
        guideCounter(adapter);
    }


    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.MOUSE_CLICKED) {
            MouseClickPayload payload
                    = (MouseClickPayload) properties;
            MouseEvent e = payload.getEvent();
            if (e.getClickCount() != 2) return;

            close();
        }
    }

}
