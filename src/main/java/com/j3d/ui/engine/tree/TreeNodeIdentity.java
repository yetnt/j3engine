package com.j3d.ui.engine.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.UUID;
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
    public final HashMap<UUID, BiConsumer<T, DefaultMutableTreeNode>> reg = new HashMap<>();

    public TreeNodeIdentity(String label, T value, BiConsumer<T, DefaultMutableTreeNode> onSelect) {
        this.label = label;
        this.value = value;
        this.onSelect = (t, defaultMutableTreeNode) -> {
            onSelect.accept(t, defaultMutableTreeNode);
            reg.values().forEach(c -> c.accept(t, defaultMutableTreeNode));
        };
    }

    public void add(UUID id, BiConsumer<T, DefaultMutableTreeNode> bc) {
        reg.put(id, bc);
    }

    public void remove(UUID uuid) {
        reg.remove(uuid);
    }

    @Override
    public String toString() {
        return label;
    }
}
