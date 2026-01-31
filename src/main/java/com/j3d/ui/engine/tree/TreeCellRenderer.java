package com.j3d.ui.engine.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (userObject instanceof TreeNodeIdentity tni) {
            setText(tni.label);
        }
        return this;
    }
}
