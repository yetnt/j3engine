package com.j3d.gen.guide.steps;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.input.mouse.AlwaysMouseOwner;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.MouseClickPayload;
import com.j3d.gen.guide.Anchor;
import com.j3d.gen.guide.GuideInfo;
import com.j3d.gen.guide.GuidePanelAdapter;
import com.j3d.gen.settings.Settings;
import com.j3d.gen.settings.types.DoubleSetting;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SceneExplStep extends GuideInfo {

    public SceneExplStep() {
        super(
                AlwaysMouseOwner.getSingleInstance()
        );
    }

    @Override
    public void build(GuidePanelAdapter adapter) {
        guideCounter(adapter);

        genericText(adapter, "(Double click to continue)");

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(
                                "What you're viewing in front of you is the scene, "
                                + "where all your 3D objects will be rendered.")
                                .addLn(JLabelRichText.LINE_BREAK)
                                .addLn(
                                        "You can use the W, A, S, D keys to move your camera around. "
                                        + "and additionally Q and E for up and down."
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .addLn(
                                        "If the movement is too slow you can change it in your settings "
                                        + " (by going to File > Settings or using the keybind ALT+S)"
                                )
                                .wrapDiv(300)
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.CENTRE | Anchor.WEST,
                Anchor.offsetRight(50), 0
        );

        // right side of screen
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
