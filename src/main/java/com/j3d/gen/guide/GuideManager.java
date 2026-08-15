package com.j3d.gen.guide;

import com.j3d.StaticRefs;
import com.j3d.ui.engine.GuidePanel;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;

public class GuideManager {
    private GuidePanel panel;

    public GuideManager(GuidePanel p) {
        panel = p;
        addCentreOffset(
                new JLabel(
                        new JLabelRichText("fdgbhfrfdgb")
                                .font("10")
                                .wrapHTML()
                ),
                0, 0
        );
        addCentreOffset(
                new JLabel(
                        new JLabelRichText("fdgbhfrfdgb")
                                .font("10")
                                .wrapHTML()
                ),
                40, 100
        );
    }

    private void addComponentAt(Component comp, int x, int y) {
        panel.add(comp);
        comp.setBounds(x, y, comp.getPreferredSize().width, comp.getPreferredSize().height);
    }

    private void addCentreOffset(Component comp, int x, int y) {
        addComponentAt(comp,
                (StaticRefs.getSceneManager().screenSize.width/2) - 200 - x,
                (StaticRefs.getSceneManager().screenSize.height/2) - 100 - y
        );
    }
}
