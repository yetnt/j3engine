package com.j3d.gen.guide.steps;

import com.j3d.engine.react.events.payloads.MouseClickPayload;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.guide.*;
import com.j3d.gen.guide.generic.DoubleClickStep;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class WelcomeStep extends DoubleClickStep {

    @Override
    public void build(GuidePanelAdapter adapter) {
        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("Welcome to ")
                                .wrapUsing(adapter.readableTextStyle)
                                .add(
                                        new JLabelRichText("J3Engine").italic()
                                )
                                .bold()
                                .underline()
                                .wrapHTML()
                ),
                Anchor.CENTRE,
                0, Anchor.offsetUp(170)
        );

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("This tutorial/guide will help you with getting started with J3Engine.")
                                .addLn(JLabelRichText.LINE_BREAK)
                                .addLn(
                                        "If you'd like to exit the tutorial, Click \"File\" in the top left-hand corner"
                                        + " and click \"Close Project\""
                                )
                                .wrapDiv(400).font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.CENTRE,
                0, Anchor.offsetUp(30)
        );

        genericText(adapter, "(Double click to continue)");

        addCompAt(
                adapter,
                image(
                        Objects.requireNonNull(WelcomeStep.class.getResource("/art/logo/J3Engine.png")),
                        0.07
                ),
                Anchor.CENTRE,
                0, Anchor.offsetDown(200)
        );
        guideCounter(adapter);
    }


}
