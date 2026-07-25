package com.j3d.ui.engine.floating.tree;

import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.generic.TreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.util.Objects;

public class SceneTreeRenderer extends TreeCellRenderer {
    private static final ImageIcon LAYER_ICON = new ImageIcon(Objects.requireNonNull(SceneTreeRenderer.class.getResource("/art/icons/layer.png")));
    private static final ImageIcon LAYER_EXPANDED_ICON = new ImageIcon(Objects.requireNonNull(SceneTreeRenderer.class.getResource("/art/icons/expanded.png")));
    private static final ImageIcon THING_ICON = new ImageIcon(Objects.requireNonNull(SceneTreeRenderer.class.getResource("/art/icons/thing.png")));
    private static final ImageIcon THING_HIDDEN_ICON = new ImageIcon(Objects.requireNonNull(SceneTreeRenderer.class.getResource("/art/icons/hidden-thing.png")));
    private static final ImageIcon LAYER_HIDDEN_ICON = new ImageIcon(Objects.requireNonNull(SceneTreeRenderer.class.getResource("/art/icons/hidden-layer.png")));

    @Override
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode node) {
            if (node.getUserObject() instanceof TreeNodeIdentity t) {
                // set visible and hidden icons
                if (t.value instanceof Layer l) {
                    if (expanded) {
                        setIcon(LAYER_EXPANDED_ICON);
                    } else if (l.isHidden()){
                        setIcon(LAYER_HIDDEN_ICON);
                    } else {
                        setIcon(LAYER_ICON);
                    }
                } else if (t.value instanceof Thing thing) {
                    if (thing.isHidden()) {
                        setIcon(THING_HIDDEN_ICON);
                    } else {
                        setIcon(THING_ICON);
                    }
                }
                setText(t.label);
            }
        }
        return this;
    }
}
