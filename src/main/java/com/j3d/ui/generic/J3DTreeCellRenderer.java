package com.j3d.ui.generic;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class J3DTreeCellRenderer extends DefaultTreeCellRenderer {
    public void init(JTree tree) {
        setBackgroundSelectionColor(J3DTheme.UI_SURFACE.color());
        setBackgroundNonSelectionColor(J3DTheme.UI_SURFACE.color());

        setTextSelectionColor(J3DTheme.TEXT_SECONDARY.color());
        setTextNonSelectionColor(J3DTheme.TEXT_PRIMARY.color());

        tree.setOpaque(true);
        tree.setBackground(J3DTheme.UI_SURFACE.color());
        tree.setForeground(J3DTheme.UI_SURFACE.color());

        setBackground(J3DTheme.UI_SURFACE.color());
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        setOpaque(true);
        return c;
    }
}
