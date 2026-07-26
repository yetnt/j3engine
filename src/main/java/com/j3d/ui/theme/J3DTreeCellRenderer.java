package com.j3d.ui.theme;

import com.j3d.ui.engine.floating.tree.SceneTreeRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Makes all {@link JTree}'s consistent with theming. Since swing just lies about setting
 * it's background and foreground normally.
 * <p>
 *     Subclasses which need icons for specific stuff can extend {@link J3DTreeCellRenderer}
 *     and override {@link #getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean)} to set the icons.
 * </p>
 * @see SceneTreeRenderer
 * @author Lehlogonolo Poole
 */
public class J3DTreeCellRenderer extends DefaultTreeCellRenderer {

    public void init(JTree tree) {
        tree.setOpaque(true);

        Consumer<Color> t = (c) -> {
            setBackgroundSelectionColor(J3DTheme.UI_SURFACE.color());
            setBackgroundNonSelectionColor(J3DTheme.UI_SURFACE.color());

            setTextSelectionColor(J3DTheme.TEXT_SECONDARY.color());
            setTextNonSelectionColor(J3DTheme.TEXT_PRIMARY.color());

            tree.setBackground(J3DTheme.UI_SURFACE.color());
            tree.setForeground(J3DTheme.UI_SURFACE.color());

            setBackground(J3DTheme.UI_SURFACE.color());
        };
        t.accept(Color.RED);

        J3DTheme.themeUpdater.add(
                J3DTheme.BACKGROUND,
                t
        );
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        setOpaque(true);
        return c;
    }
}
