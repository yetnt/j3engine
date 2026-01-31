package com.j3d.ui.engine.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TreeNodeIdentity<T> {
    /**
     * The label of the node.
     */
    public final String label;
    /**
     * The value of the node.
     */
    public final T value;
    /**
     * The action to perform when the node is selected.
     */
    public final BiConsumer<T, DefaultMutableTreeNode> onSelect;

    public TreeNodeIdentity(String label, T value, BiConsumer<T, DefaultMutableTreeNode> onSelect) {
        this.label = label;
        this.value = value;
        this.onSelect = onSelect;
    }

    @Override
    public String toString() {
        return label;
    }
}
