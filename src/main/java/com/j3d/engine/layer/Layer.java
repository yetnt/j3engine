package com.j3d.engine.layer;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.react.actions.DirtyAction;
import com.j3d.engine.react.actions.DirtyVoidAction;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.ConstructorAction;
import com.j3d.ui.engine.tree.TreeNodeIdentity;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * A {@code Layer} is a fundamental concept in the rendering pipeline, representing a
 * collection of {@link Thing} instances that are rendered together. The
 * {@link Renderer} processes these layers in a specific order, drawing the
 * contents of each layer to the screen. By organizing {@code Thing}s into
 * layers, you can control their stacking order and visibility.
 * <p>
 * The {@code Layer} class extends {@link ArrayList}, providing a versatile and
 * efficient way to manage the objects within it. You can add, remove, and
 * reorder objects in a layer to dynamically change the scene's composition.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>
 *         <b>Object Grouping:</b> Layers allow you to group related
 *         {@link GObject}s, making it easier to manage complex scenes. For
 *         example, you could have separate layers for the background, main
 *         characters, and UI elements.
 *     </li>
 * </ul>
 *
 * @see Renderer
 * @see GObject
 * @see ArrayDeque
 */
public class Layer extends ArrayList<Thing> implements Interactable {

    private final String identifier;

    public static final String backgroundId = "BACKG";
    private final BiConsumer<Layer, DefaultMutableTreeNode> onSelectCallback = (o, t) -> {
        J3DSettings.log.println("Layer " + o.getIdentifier() + " was selected in the tree.");
    };

    private boolean hidden = false;
    private boolean forDeletion = false;

    private TreeNodeIdentity<Layer> treeNodeIdentity;
    private DefaultMutableTreeNode treeNode;


    /**
     * Default Constructor
     * @param id The identifier of the layer.
     */
    public Layer(String id) {
        identifier = id;
        final String idFinal = id;
        if (id.equals(backgroundId))
            return; // Do not follow through.
        treeNodeIdentity = new TreeNodeIdentity<>(
                id, this, onSelectCallback);
        treeNode = Static.layerTree.addNode(null, treeNodeIdentity);
        Renderer.history.add(
                new ConstructorAction() {
                    @Override
                    public void cleanup() {
                        // Layer was fully discarded, instantDelete everything within it.
                        if (isForDeletion())
                            layer.instantDelete();
                    }

                    final Layer layer = Layer.this;
                    @Override
                    public Void run() {
                        layer.setForDeletion(false);
                        DefaultMutableTreeNode node = Static.layerTree.addNode(null, treeNodeIdentity);
                        layer.treeNode = node;
                        return null;
                    }

                    @Override
                    public void undo() {
                        layer.setForDeletion(true);
                        Static.layerTree.removeNode(layer.treeNode);
                    }

                    @Override
                    public String getDescription() {
                        return "Construct:Layer-" + id;
                    }
                }
        );
    }

    /**
     * Default Constructor. This constructor should not be used by a user as this instantiates the default layer.
     */
    public Layer() {
        identifier = "LAYER-0";
        treeNodeIdentity = new TreeNodeIdentity<>(
                "LAYER-0", this, onSelectCallback
        );
        treeNode = Static.layerTree.addNode(null, treeNodeIdentity);
        Renderer.history.add(
                new ConstructorAction() {
                    @Override
                    public void cleanup() {
                        throw new IllegalStateException("The main layer should never have to be cleaned up.");
                    }
                    @Override
                    public boolean isReversible() {
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Construct:Layer-Default";
                    }
                }
        );
    }

    /**
     * Returns the identifier of this layer
     * @return The identifier of this layer
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Merges the contents of another {@code Layer} into this layer. All
     * {@link GObject}s from the {@code otherlayer} are added to this layer.
     * The {@code otherlayer} itself remains unchanged.
     *
     * @param otherlayer The {@code Layer} whose contents are to be merged into this layer.
     * @return An {@link Action} representing the squash operation, which can be undone if needed.
     */
    public DirtyAction<Layer> squashWith(Layer otherlayer) {
        final Layer current = this;
        final Layer other = otherlayer;
        return new DirtyAction<>() {
            @Override
            public void cleanup() throws Exception {
                if (other.isForDeletion())
                    other.instantDelete();
            }

            // Keep the objects that were added for undo functionality
            private ArrayList<Thing> addedObjects = new ArrayList<>(otherlayer);
            @Override
            public Layer run() {
                current.addAll(otherlayer);
                other.setForDeletion(true); // To the user it won't be available.
                return current;
            }

            @Override
            public void undo() {
                current.removeAll(addedObjects);
                other.setForDeletion(false);
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Layer-"+ current.getIdentifier() + ":SquashWith-" + other.getIdentifier();
            }
        };
    }

    /**
     * Draws all {@link Thing} instances in this layer using the provided
     * {@link Graphics2D} context. The drawing order is determined by the
     * distance of each {@code Thing}'s centroid from the camera position,
     * ensuring proper depth representation in the rendered scene.
     *
     * @param graphics2D The {@code Graphics2D} context used for drawing.
     */
    public void draw(Graphics2D graphics2D) {
        if (!getIdentifier().equals(backgroundId))
            sort(Comparator.comparingDouble(t -> t.getCentroid().distance(Static.camera.getPosition())));
        if (isHidden() || isForDeletion()) return;
        for (Thing o : this.reversed()) {
            o.draw(graphics2D);
        }
    }

    @Override
    public void instantDelete() {
        Static.renderer.layers.remove(this);
        for (Thing t : this) {
            t.instantDelete();
        }
    }

    public TreeNodeIdentity<Layer> getIdentity() {
        return treeNodeIdentity;
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        return treeNode;
    }

    @Override
    public BiConsumer<? extends Interactable, DefaultMutableTreeNode> getOnSelect() {
        return onSelectCallback;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isForDeletion() {
        return forDeletion;
    }

    @Override
    public void setForDeletion(boolean forDeletion) {
        this.forDeletion = forDeletion;
    }

    @Override
    public Action<Boolean> toggleVisibility() {
        final Layer l = this;
        return new Action<>() {
            final boolean oldState = l.hidden;
            @Override
            public Boolean run() {
                l.setHidden(!l.hidden);
                stream().map(Thing::getObjects)
                        .flatMap(Collection::stream)
                        .filter(o -> o instanceof GTri)
                        .map(o -> (GTri) o)
                        .forEach(o -> o.setHidden(l.hidden));
                return l.hidden;
            }

            @Override
            public void undo() {
                l.setHidden(oldState);
                stream().map(Thing::getObjects)
                        .flatMap(Collection::stream)
                        .filter(o -> o instanceof GTri)
                        .map(o -> (GTri) o)
                        .forEach(o -> o.setHidden(oldState));

            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Layer:VisibilityToggle";
            }
        };
    }

    @Override
    public DirtyVoidAction deleteLater() {
        final Layer l = this;
        return new DirtyVoidAction() {
            @Override
            public void cleanup() throws Exception {
                if (isForDeletion()) l.instantDelete();
            }

            @Override
            public Void run() {
                l.setForDeletion(true);
                Static.layerTree.removeNode(l.treeNode);
                return null;
            }

            @Override
            public void undo() {
                l.setForDeletion(false);
                l.treeNode = Static.layerTree.addNode(null, l.treeNodeIdentity);
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Layer:Delete";
            }
        };
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
