package com.j3d.gen.guide;

import com.j3d.ui.engine.GuidePanel;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;

public class GuidePanelAdapter {
    private GuidePanel panel;
    public final JLabelRichText readableTextStyle = new JLabelRichText().font(
            J3DTheme.TEXT_PRIMARY.color(), "10"
    );

    protected GuidePanelAdapter(GuidePanel p) {
        panel = p;
    }

    public void addComponentAt(Component comp, int x, int y) {
        panel.add(comp);
        comp.setBounds(x, y, comp.getPreferredSize().width, comp.getPreferredSize().height);
    }

    public void addCentreOffset(Component comp, int x, int y) {
        addComponentAt(comp,
                (this.panel.getWidth()/2) - x,
                (this.panel.getHeight()/2) - y
        );
    }

    public int getWidth() {
        return panel.getWidth();
    }

    public int getHeight() {
        return panel.getHeight();
    }

    public void remove(Component component) {
        panel.remove(component);
    }

    public void repaint() {
        panel.repaint();
    }
}
