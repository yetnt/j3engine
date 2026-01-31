package com.j3d.ui.engine.tree;

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
    public final Consumer<T> onSelect;

    public TreeNodeIdentity(String label, T value, Consumer<T> onSelect) {
        this.label = label;
        this.value = value;
        this.onSelect = onSelect;
    }

    @Override
    public String toString() {
        return label;
    }
}
