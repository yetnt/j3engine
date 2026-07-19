package com.j3d.engine.geometry.geo3d;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.react.actions.DirtyVoidAction;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.spec.TriUpdatedBroadcast;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.ConstructorAction;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.gen.properties.HasProperties;
import com.j3d.gen.properties.Property;
import com.j3d.StaticConfig;
import com.j3d.storage.files.protocol.proj.PF1;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.ui.dialog.Spinner;
import com.j3d.ui.engine.floating.tree.TreeNodeIdentity;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A Thing, or more commonly in engine lingo, a mesh, is an {@link Interactable}
 * which makes up an entire 3d object made of {@link GTri}s, {@link GPoint}s and {@link GLine}s.
 * <p>
 *     A Thing, can be transformed in any means necessary and is the only "object"
 *     which the user can actually interact with. They can interact with GObjects but a Thing
 *     is the first tangible <i>thing</i>. (pun intended)
 * </p>
 * <p>
 *     A Thing is the first user tangible object. So it naturally has to interact with
 *     the app in many different ways whther it be showing itself in the {@link javax.swing.JTree}
 *     or being written to a {@link ProjectFile}
 * </p>
 * @implSpec
 *     As a user tangible object, most operations return an {@link Action} which
 *     represent the transform or editing of the Thing and should be recorded to
 *     the history by the caller method otherwise the user cannot undo and redo
 *     said actions.
 * @author Lehlogonolo Poole
 * @see Interactable
 * @see Vector3
 * @see Action
 */
public class Thing implements Interactable, HasProperties {

    /** The centroid of the Thing, calculated from the GPoints it contains. */
    private Vector3 centroid;

    /** The unique identifier for this Thing. */
    private UUID id;

    /**
     * The name of this Thing.
     */
    private String name = "Thing";

    private final ArrayList<Property<?, ?>> properties = new ArrayList<>();

    /**
     * What the thing should do when it gets selected within the tree GUI
     */
    private final BiConsumer<Thing, DefaultMutableTreeNode>  onSelectCallback =
            (o, t) -> {
                if (this.isBg || this.hidden) return;
                StaticRefs.getLog().println(name + " thing was selected in the tree.");
                StaticRefs.getSceneManager().select(this);
                StaticRefs.getMainPanel().repaint();
            };

    /**
     * Gets the name of this Thing.
     * @return The name of this Thing.
     */
    public String getName() {
        return name;
    }

    /** The master list of GObjects that compose this 3D Thing. */
    private final ArrayList<GObject> objects = new ArrayList<>();

    /** The list of points that compose this 3D Thing. */
    private final List<GPoint> points = new ArrayList<>();

    /**
     * Whether all triangles that get added to this Thing should be single sided or not.
     */
    private boolean solid = false;

    /** The parent layer of this Thing. */
    private Layer parent;

    /** A Flag set by the Thing itself to check whether its part of the background. if so it only draws the
     * axes and the background.
     */
    private boolean isBg = false;

    /**
     * Governed by {@link Interactable}, whether this Thing is hidden or not.
     */
    private boolean hidden = false;
    /**
     * Governed by {@link Interactable}, whether this Thing is for deletion or not.
     */
    private boolean forDeletion = false;

    /**
     * Required by {@link Interactable}, The Tree Node Identity object defining this Thing
     * within the Tree GUI.
     */
    private TreeNodeIdentity<Thing> treeNodeIdentity;
    /**
     * Required by {@link Interactable}, The actual tree node reference
     * for this Thing within the Tree GUI.
     */
    private DefaultMutableTreeNode treeNode;

    /**
     * Constructs a Thing.
     * @implSpec This is used by {@link PF1#readFile(String, String, Spinner)} during a project file read and should only be used in that case.
     * @param name The name of the Thing defined in the file.
     * @param id The ID of the Thing defined in the file.
     * @param hidden Whether the Thing is hidden or not.
     * @param l The parent layer of the Thing.
     * @param sceneManager The sceneManager instance.
     * @return A Thing
     */
    public static Thing fromRaw(String name, String id, boolean hidden, Layer l, SceneManager sceneManager) {
        Thing t = new Thing(sceneManager, l, name, false);
        t.setHidden(hidden);
        t.setId(UUID.fromString(id));
        return t;
    }

    /**
     * Sets this thing's unique identifier
     * @implSpec This is intended to be used when this is created from
     * file loading or anything where it hasnt had a UUID attached to it already.
     * Otherwise the UUID is treated as immutable.
     * @param uuid The new UUID
     * @see PF1#readFile(String, String, Spinner)
     */
    private void setId(UUID uuid) {
        this.id = uuid;
    }

    @Override
    public void invokeSwingHooks() {
        treeNodeIdentity = new TreeNodeIdentity<>(
                name, this, onSelectCallback
        );
        treeNode = StaticRefs.getLayerTree().addNode(parent.getTreeNode(), treeNodeIdentity);
        toggleSaved();
        addProps();
        SceneManager.history.add(
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
                        thing.treeNode = StaticRefs.getLayerTree().addNode(parent.getTreeNode(), treeNodeIdentity);
                        objectsStream()
                                .filter(s -> s instanceof GTri)
                                .map(g -> (GTri)g)
                                .forEach(TriStateArea::register);
                        return null;
                    }

                    @Override
                    public void undo() {
                        setForDeletion(true);
                        StaticRefs.getLayerTree().removeNode(thing.treeNode);
                        objectsStream()
                                .filter(s -> s instanceof GTri)
                                .map(g -> (GTri)g)
                                .forEach(TriStateArea::unregister);
                    }

                    @Override
                    public String getDescription() {
                        return "Construct:Thing";
                    }

                    private final LocalTime now = LocalTime.now();
                    @Override
                    public LocalTime getTime() {
                        return now;
                    }
                }
        );
    }

    /**
     * Constructs a Thing.
     * @param sceneManager The sceneManager instance.
     * @param l The parent layer of the Thing.
     * @param name The name of the Thing.
     * @param invokeSwingHooks Whether to run GUI related hooks
     */
    public Thing(SceneManager sceneManager, Layer l, String name, boolean invokeSwingHooks) {
        toggleSaved();
        l = l == null ? sceneManager.layers.get(1) : l;
        if (l.getIdentifier().equals(Layer.BACKGROUND_ID)) {
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

    /**
     * Constructs a Thing and runs GUI related hooks.
     * @param sceneManager The sceneManager instance.
     * @param l The parent layer of the Thing.
     * @param name The name of the Thing.
     */
    public Thing(SceneManager sceneManager, Layer l, String name) {
        toggleSaved();
        l = l == null ? sceneManager.layers.get(1) : l;
        if (l.getIdentifier().equals(Layer.BACKGROUND_ID)) {
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
        toggleSaved();
        if (gObjects.length == 0) {
            System.out.println("bug biyvh");
        }
        Collections.addAll(objects, gObjects);
        ArrayList<Vector3> pts = new ArrayList<>();
        for (GObject ob : gObjects) {
            if (ob instanceof GPoint p) {
                pts.add(p.getPivot());
                points.add(p);
            } else if (ob instanceof GTri tri && solid) {
                tri.setDoubleSided(false);
            }
        }
        if (!pts.isEmpty()) {
            Vector3 sum = Vector3.reduceToVector3(pts, Vector3::add);
            centroid = sum.div(pts.size());
        }
        return this;
    }

    /**
     * Draws this Thing to the screen.
     * @param graphics2D The Graphics2D instance
     * @implSpec This is caled by {@link Layer#draw(Graphics2D)}
     */
    public void draw(Graphics2D graphics2D) {
        if (isBg) {
//            graphics2D.setColor(new Color(52, 52, 52));
            graphics2D.setColor(J3DTheme.BACKGROUND.color());
            graphics2D.fillRect(0, 0, StaticConfig.screenSize.width, StaticConfig.screenSize.height);
            StaticRefs.getSceneManager().axis(graphics2D, StaticRefs.getCamera());
            StaticRefs.getSceneManager().axisGrid(graphics2D, StaticRefs.getCamera());
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

    /**
     * Returns the GObjects that compose this Thing.
     * @return The GObjects that compose this Thing.
     */
    public ArrayList<GObject> getObjects() {
        return objects;
    }

    /**
     * Returns the centroid of this Thing.
     * @return The centroid of this Thing.
     */
    public Vector3 getCentroid() {
        return centroid;
    }

    @Override
    public ArrayList<Property<?, ?>> getProperties() {
        return properties;
    }

    /**
     * Solidifies the Thing by setting all its {@link GTri}s to be single-sided.
     * @return This Thing, for method chaining.
     */
    public Thing solidify() {
        solid = true;
        toggleSaved();
        objectsStream()
                .filter(s -> s instanceof GTri)
                .map(g -> (GTri)g)
                .forEach(t -> t.setDoubleSided(false));
        return this;
    }

    private void addProps() {
        properties.add(
                new Property<>("Thing Name", this::getName, Thing.class)
                        .holds(String.class)
                        .setNewValueConsumer(this::setName)
                        .setDescription("The name given to this Thing")
        );
        properties.add(
                new Property<>("Thing ID", this::getId, Thing.class)
                        .holds(UUID.class)
                        .setDescription("The id given to this Thing")
                        .constant()
        );
        properties.add(
                new Property<>("Object Amount", getObjects()::size, Thing.class)
                        .holds(Integer.class)
                        .setDescription("The amount of objects within this Thing")
                        .constant()
        );
        properties.add(
                new Property<>("Centroid", this::getCentroid, Thing.class)
                        .holds(Vector3.class)
                        .setDescription("The centroid of this Thing, usually its geometric centre")
                        .constant()
        );
    }

    private void setName(String s) {
        name = s;
        getIdentity().setLabel(s);
        toggleSaved();
    }

    /**
     * Creates a copy of this Thing, adding its GObjects to the specified sceneManager and layer.
     * @param sceneManager The sceneManager to associate the new Thing with.
     * @param l The layer to add the new Thing to.
     * @return An Action that performs the copy operation and itself returns the
     * new Thing.
     */
    public Action<Thing> copy(SceneManager sceneManager, Layer l) {
        toggleSaved();
        final Thing current = this;
        return new Action<Thing>() {
            private Thing newThing;

            @Override
            public Thing run() {
                newThing = new Thing(sceneManager, l, current.name + " copy").addObjs(objects.toArray(GObject[]::new));
                return newThing;
            }

            @Override
            public void undo() {
                sceneManager.removeThing(newThing);
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return "Thing:Copy";
            }

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    /**
     * Notifies all GTri objects within this Thing that they have been updated.
     */
    private void notifyTris() {
        for (GTri tri : objects.stream().filter(o -> o instanceof GTri).map(o -> (GTri) o).toList()) {
            tri.broadcast(EventType.OBJ_UPDATED, new TriUpdatedBroadcast(tri, StaticRefs.getSceneManager()));
        }
    }

    /**
     * Scales the Thing by a uniform factor around its centroid.
     * @param scale The uniform scaling factor.
     * @return An Action which performs the scae operation.
     */
    public VoidAction scale(double scale) {
        toggleSaved();
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    /**
     * Scales the Thing by a vector factor around its centroid.
     * @param scale The scaling vector, where each component scales along its respective axis.
     * @return An Action which performs the scale operation.
     */
    public VoidAction scale(Vector3 scale) {
        toggleSaved();
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    /**
     * Translates the Thing by a given vector.
     * @param v The translation vector.
     * @return An Action which performs the translation operation.
     */
    public VoidAction translate(Vector3 v) {
        toggleSaved();
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    /**
     * Rotates the Thing around a given axis by a given angle.
     * @param axis The rotation axis.
     * @param angleDegrees The rotation angle in degrees.
     * @return An Action which performs the rotation operation.
     */
    public VoidAction rotate(Vector3 axis, double angleDegrees) {
        toggleSaved();
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    /**
     * @implSpec Deletes all underlying GObjects. This overrides all history functionality
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
        toggleSaved();
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    @Override
    public DirtyVoidAction deleteLater() {
        toggleSaved();
        final Thing t = this;
        final DefaultMutableTreeNode parentLayerNode = (DefaultMutableTreeNode) treeNode.getParent();
        return new DirtyVoidAction() {
            @Override
            public void cleanup() throws Exception {
                 StaticRefs.getSceneManager().removeThing(t);
                 t.instantDelete();
            }

            @Override
            public Void run() {
                t.setForDeletion(true);
                StaticRefs.getLayerTree().removeNode(treeNode);
                return null;
            }

            @Override
            public void undo() {
                t.setForDeletion(false);
                DefaultMutableTreeNode node = StaticRefs.getLayerTree().addNode(parentLayerNode, treeNodeIdentity);
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

            private final LocalTime now = LocalTime.now();
            @Override
            public LocalTime getTime() {
                return now;
            }
        };
    }

    public Stream<GObject> objectsStream() {
        return objects.stream();
    }

    public boolean isSolid() {
        return solid;
    }
}
