package com.j3d.engine.geometry.geo3d;

import com.j3d.J3DSettings;
import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.react.actions.DirtyVoidAction;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.spec.TriUpdatedBroadcast;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.ConstructorAction;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.engine.tree.TreeNodeIdentity;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Represents a 3D object composed of multiple 2D geometric objects (GObjects).
 */
public class Thing implements Interactable {

    /** The centroid of the Thing, calculated from the GPoints it contains. */
    private Vector3 centroid;

    /** The unique identifier for this Thing. */
    private UUID id;

    private String name = "Thing";
    private final BiConsumer<Thing, DefaultMutableTreeNode>  onSelectCallback =
            (o, t) -> {
                if (this.isBg || this.hidden) return;
                J3DSettings.log.println(name + " thing was selected in the tree.");
                Static.renderer.select(this);
                Static.mainPanel.repaint();
            };

    public String getName() {
        return name;
    }

    /** The list of 2D geometric objects that compose this 3D Thing. */
    private final ArrayList<GObject> objects = new ArrayList<>();

    private final List<GPoint> points = new ArrayList<>();

    private Layer parent;

    /** A Flag set by the Thing itself to check whether its part of the background. if so it only draws the
     * axes and the background.
     */
    private boolean isBg = false;

    private boolean hidden = false;
    private boolean forDeletion = false;

    private TreeNodeIdentity<Thing> treeNodeIdentity;
    private DefaultMutableTreeNode treeNode;

    public static Thing fromRaw(String name, String id, boolean hidden, Layer l, Renderer renderer) {
        Thing t = new Thing(renderer, l, name, false);
        t.setHidden(hidden);
        t.setId(UUID.fromString(id));
        return t;
    }

    private void setId(UUID uuid) {
        this.id = uuid;
    }

    @Override
    public void invokeSwingHooks() {
        treeNodeIdentity = new TreeNodeIdentity<>(
                name, this, onSelectCallback
        );
        treeNode = Static.layerTree.addNode(parent.getTreeNode(), treeNodeIdentity);
        Renderer.history.add(
                new ConstructorAction() {
                    @Override
                    public void cleanup() throws Exception {
                        parent.remove(thing);
                        thing.instantDelete();
                    }

                    private final Thing thing = Thing.this;
                    @Override
                    public Void run() {
                        // will be called after undo, so we need to re-add the thing
                        setForDeletion(false);
                        DefaultMutableTreeNode node = Static.layerTree.addNode(parent.getTreeNode(), treeNodeIdentity);
                        thing.treeNode = node;
                        return null;
                    }

                    @Override
                    public void undo() {
                        setForDeletion(true);
                        Static.layerTree.removeNode(thing.treeNode);
                    }

                    @Override
                    public String getDescription() {
                        return "Construct:Thing";
                    }
                }
        );
    }

    public Thing(Renderer renderer, Layer l, String name, boolean invokeSwingHooks) {
        l = l == null ? renderer.layers.get(1) : l;
        if (l.getIdentifier().equals(Layer.backgroundId)) {
            isBg = true;
        }
        l.add(this);
        this.name = name;
        id = UUID.randomUUID();
        if (isBg)
            return;
        // Add to history for undo/redo functionality
        parent = l;
        if (invokeSwingHooks)
            invokeSwingHooks();
    }

    public Thing(Renderer renderer, Layer l, String name) {
        l = l == null ? renderer.layers.get(1) : l;
        if (l.getIdentifier().equals(Layer.backgroundId)) {
            isBg = true;
        }
        l.add(this);
        this.name = name;
        id = UUID.randomUUID();
        if (isBg)
            return;
        // Add to history for undo/redo functionality
        parent = l;
        invokeSwingHooks();

    }

    /**
     * Adds one or more GObjects to this Thing.
     * @param gObjects The GObjects to add.
     */
    public Thing addObjs(GObject ...gObjects) {
        Collections.addAll(objects, gObjects);
        ArrayList<Vector3> pts = new ArrayList<>();
        for (GObject ob : gObjects) {
            if (ob instanceof GPoint p) {
                pts.add(p.getPivot());
                points.add(p);
            }
        }
        if (!pts.isEmpty()) {
            Vector3 sum = Vector3.reduce(pts, Vector3::add);
            centroid = sum.div(pts.size());
        }
        return this;
    }

    public void draw(Graphics2D graphics2D) {
        if (isBg) {
//            graphics2D.setColor(new Color(52, 52, 52));
            graphics2D.setColor(J3DTheme.CHARCOAL_BLUE.color());
            graphics2D.fillRect(0, 0, J3DSettings.screenSize.width, J3DSettings.screenSize.height);
            Static.renderer.axis(graphics2D, Static.camera);
            return;
        }

        if (isForDeletion() || isHidden()) return;
        for (GObject o : objects) {
            if (o instanceof GTri t) {
                TriStateArea.addToQueue(t);
            }
        }
    }

    /**
     * Returns the unique identifier of this Thing.
     * @return The UUID of this Thing.
     */
    public UUID getId() {
        return id;
    }

    public ArrayList<GObject> getObjects() {
        return objects;
    }

    public Vector3 getCentroid() {
        return centroid;
    }

    /**
     * Creates a copy of this Thing, adding its GObjects to the specified renderer and layer.
     * @param renderer The renderer to associate the new Thing with.
     * @param l The layer to add the new Thing to.
     * @return An Action that performs the copy operation.
     */
    public Action<Thing> copy(Renderer renderer, Layer l) {
        final Thing current = this;
        return new Action<Thing>() {
            private Thing newThing;

            @Override
            public Thing run() {
                newThing = new Thing(renderer, l, current.name + " copy").addObjs(objects.toArray(GObject[]::new));
                return newThing;
            }

            @Override
            public void undo() {
                renderer.removeThing(newThing);
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:Copy";
            }
        };
    }

    /**
     * Notifies all GTri objects within this Thing that they have been updated.
     */
    private void notifyTris() {
        for (GTri tri : objects.stream().filter(o -> o instanceof GTri).map(o -> (GTri) o).toList()) {
            tri.broadcast(EventType.OBJ_UPDATED, new TriUpdatedBroadcast(tri, Static.renderer));
        }
    }

    /**
     * Scales the Thing by a uniform factor around its centroid.
     * @param scale The uniform scaling factor.
     */
    public VoidAction scale(double scale) {
        return new VoidAction() {
            private final ArrayList<Vector3> originalPositions = new ArrayList<>();
            @Override
            public Void run() {
                for (GPoint p : points) {
                    originalPositions.add(p.getPivot().copy());
                    p.setPivot(p.getPivot().sub(centroid).mult(scale).add(centroid));
                }
                notifyTris();
                return null;
            }

            @Override
            public void undo() {
                for (int i = 0; i < points.size(); i++) {
                    points.get(i).setPivot(originalPositions.get(i));
                }
                notifyTris();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:ScaleUniform";
            }
        };
    }

    /**
     * Scales the Thing by a vector factor around its centroid.
     * @param scale The scaling vector, where each component scales along its respective axis.
     */
    public VoidAction scale(Vector3 scale) {
        return new VoidAction() {
            private final ArrayList<Vector3> originalPositions = new ArrayList<>();
            @Override
            public Void run() {
                for (GPoint p : points) {
                    originalPositions.add(p.getPivot().copy());
                    p.setPivot(p.getPivot().sub(centroid).mult(scale).add(centroid));
                }
                notifyTris();
                return null;
            }

            @Override
            public void undo() {
                for (int i = 0; i < points.size(); i++) {
                    points.get(i).setPivot(originalPositions.get(i));
                }
                notifyTris();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:ScaleVector";
            }
        };
    }

    /**
     * Translates the Thing by a given vector.
     * @param v The translation vector.
     */
    public VoidAction translate(Vector3 v) {
        return new VoidAction() {
            private final ArrayList<Vector3> originalPositions = new ArrayList<>();
            @Override
            public Void run() {
                for (GPoint p : points) {
                    originalPositions.add(p.getPivot().copy());
                    p.setPivot(p.getPivot().add(v));
                }
                notifyTris();
                return null;
            }

            @Override
            public void undo() {
                for (int i = 0; i < points.size(); i++) {
                    points.get(i).setPivot(originalPositions.get(i));
                }
                notifyTris();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:ScaleVector";
            }
        };
    }

    public VoidAction rotate(Vector3 axis, double angleDegrees) {
        return new VoidAction() {
            private final ArrayList<Vector3> originalPositions = new ArrayList<>();
            @Override
            public Void run() {
                for (GPoint p : points) {
                    originalPositions.add(p.getPivot().copy());
                    Vector3 dir = p.getPivot().sub(centroid);
                    dir = dir.rotateAroundAxis(axis, angleDegrees);
                    p.setPivot(centroid.add(dir));
                }
                notifyTris();
                return null;
            }

            @Override
            public void undo() {
                for (int i = 0; i < points.size(); i++) {
                    points.get(i).setPivot(originalPositions.get(i));
                }
                notifyTris();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:Rotate";
            }
        };
    }

    /**
     * Deletes all underlying GObjects. This overrides all history functionality
     * and should never be used by a user.
     */
    @Override
    public void instantDelete() {
        for (GObject o : objects) {
            if (o instanceof GTri tri)
                tri.deleteSelf();
        }
    }

    @Override
    public TreeNodeIdentity<Thing> getIdentity() {
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
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (hidden) {
            for (GObject o : objects) {
                if (o instanceof GTri tri)
                    tri.setHidden(true);
            }
        } else {
            for (GObject o : objects) {
                if (o instanceof GTri tri)
                    tri.setHidden(false);
            }
        }
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
        final Thing t = this;
        return new Action<Boolean>() {
            final boolean oldState = t.hidden;
            @Override
            public Boolean run() {
                t.setHidden(!t.hidden);
                notifyTris();
                return t.hidden;
            }

            @Override
            public void undo() {
                t.setHidden(oldState);
                notifyTris();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:VisibilityToggle";
            }
        };
    }

    @Override
    public DirtyVoidAction deleteLater() {
        final Thing t = this;
        final DefaultMutableTreeNode parentLayerNode = (DefaultMutableTreeNode) treeNode.getParent();
        return new DirtyVoidAction() {
            @Override
            public void cleanup() throws Exception {
                 Static.renderer.removeThing(t);
                 t.instantDelete();
            }

            @Override
            public Void run() {
                t.setForDeletion(true);
                Static.layerTree.removeNode(treeNode);
                return null;
            }

            @Override
            public void undo() {
                t.setForDeletion(false);
                DefaultMutableTreeNode node = Static.layerTree.addNode(parentLayerNode, treeNodeIdentity);
                t.treeNode = node;
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:Delete";
            }
        };
    }
}
