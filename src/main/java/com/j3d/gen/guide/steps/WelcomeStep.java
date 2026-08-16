package com.j3d.gen.guide.steps;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.input.MouseClickPayload;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.gen.guide.*;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class WelcomeStep extends GuideInfo {

    public WelcomeStep(GuideManager guideManager, GuideFlow f) {
        super(
                f, AlwaysMouseOwner.getInstance() // listen to the always mouse owner for clicks.
        );
    }

    @Override
    public void build(GuidePanelAdapter adapter) {
        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText("Traveller.")
                                .wrapUsing(adapter.readableTextStyle)
                                .wrapHTML()
                ),
                Anchor.NORTH | Anchor.EAST
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
            JOptionPane.showMessageDialog(
                    StaticRefs.getMainFrame(),
                    "IT WORKSSSSSSSSSSSSSSss"
            );
        }
    }
}
