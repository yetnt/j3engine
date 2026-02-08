package com.j3d.engine.layer;

import com.j3d.engine.react.actions.VoidAction;

import java.util.ArrayList;
import java.util.List;

public class LayerList extends ArrayList<Layer> {

    public LayerList() {
        super();
    }
    public LayerList(int initialCapacity) {
        super(initialCapacity);
    }

    public static LayerList from(List<Layer> collect) {
        LayerList list = new LayerList();
        list.addAll(collect);
        return list;
    }

    @Override
    public void clear() {
        Layer bg = getFirst();
        super.clear();
        add(bg);
    }

    /**
     * Finds a {@link Layer} by its identifier.
     * @param id The identifier of the layer to find.
     * @return The {@link Layer} with the matching identifier, or {@code null} if no such layer is found.
     */
    public Layer find(String id) {
        for (Layer layer : this) {
            if (layer.getIdentifier().equals(id)) {
                return layer;
            }
        }
        return null;
    }

    private void internalSwap(Layer l1, Layer l2) {
        int index1 = this.indexOf(l1);
        int index2 = this.indexOf(l2);
        if (index1 == -1 || index2 == -1) {
            throw new IllegalArgumentException("One or both layers not found in the list");
        }
        this.set(index1, l2);
        this.set(index2, l1);
    }

    /**
     * Swaps the positions of two layers in the list.
     * @param l1 The first layer to swap.
     * @param l2 The second layer to swap.
     * @return A {@link VoidAction} that performs the swap and can be undone.
     * @throws IllegalArgumentException if either layer is not found in the list.
     */
    public VoidAction swap(Layer l1, Layer l2) {

        return new VoidAction() {
            @Override
            public Void run() {
                internalSwap(l1, l2);
                return null;
            }

            @Override
            public void undo() {
                internalSwap(l2, l1);
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Layers:Swap";
            }
        };
    }

    public VoidAction move(Layer layer, LayerMoveOperation moveOperation) {
        final LayerList layers = this;
        final int index = layers.indexOf(layer);
        if (index == 0)
            throw new IllegalStateException("Default layer cannot be moved.");
        if (index == -1)
            throw new IllegalArgumentException("Layer not found in the list");
        return new VoidAction() {
            @Override
            public Void run() {
                switch (moveOperation) {
                    case FORWARD -> {
                        if (index <= 1) throw new IllegalStateException("Layer cannot move forward");
                        layers.internalSwap(layer, layers.get(index - 1));
                    }
                    case BACKWARD -> {
                        if (index == layers.size() - 1) throw new IllegalStateException("Layer cannot move backward");
                        layers.internalSwap(layer, layers.get(index + 1));
                    }
                    case TO_TOP -> {
                        if (layers.size() == 2) return null;

                        int from = index;
                        int to = 1;

                        for (int i = from; i > to; i--) {
                            layers.set(i, layers.get(i-1));
                        }
                        layers.set(to, layer);
                    }
                    case TO_BOTTOM -> {
                        if (layers.size() <= 2) return null;
                        int from = index;
                        int to = layers.size() - 1;

                        for (int i = from; i < to; i++) {
                            layers.set(i, layers.get(i+1));
                        }
                        layers.set(to, layer);
                    }
                }
                return null;
            }

            @Override
            public void undo() {

            }

            @Override
            public boolean isReversible() {
                return false;
            }

            @Override
            public String getDescription() {
                return "";
            }
        };
    }

    //TODO: Undo for move operation.
}
