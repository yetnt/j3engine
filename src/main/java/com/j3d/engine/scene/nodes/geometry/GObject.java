package com.j3d.engine.scene.nodes.geometry;

import com.j3d.engine.scene.SceneObject;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.copy.CanCopy;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.geometry.Pure;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.gen.properties.Property;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.dialog.Spinner;

import java.awt.*;
import java.util.*;

/**
 * Graphics Object is an abstract class that represents any actual tangible
 * Geometry in the 3d space (Cartesian space) that a user can see and
 * interact with. This only has 3 extenders, {@link GPoint}, {@link GLine}
 * and {@link GTri} as these form the base objects and cannot be broken
 * down further.
 * <p>
 *     Any GObject also has event listening and emitting capabilities
 *     as an extender of {@link EventEmitter} and an implementor of
 *     {@link EventListener}. It is up to the child classes to decide
 *     which events it wants to listen to or emit.
 * </p>
 * @author Lehlogonolo Poole
 * @see EventListener
 * @see EventEmitter
 * @see GPoint
 * @see GLine
 * @see GTri
 */
public abstract class GObject extends EventEmitter implements EventListener, CanCopy, SceneObject {
    protected Color col = Color.BLACK;
    /**
     * The pivot point of this geometry. Unless a {@link GPoint} where this represents the actual location
     * of the point.
     */
    private Vector3 pivot;
    /**
     * A Unique UUID to identify this geometry.
     */
    private UUID Id;
    protected ArrayList<Property<?, ?>> properties = new ArrayList<>();

    /**
     * Default Constructor.
     */
    public GObject() {
        Id = UUID.randomUUID();
        addProps();
    }

    /**
     * Default Constructor. with color
     * @param colour The colour.
     */
    public GObject(Color colour) {
        this();
        col = colour;
        // addprops()
    }

    public <T extends Pure> ArrayList<RenderState<T, GObject>> genericRenderStateList() {
        return new ArrayList<>();
    }

    // GLine and GTri make this constant.
    protected Property<Vector3, GObject> pivotProperty =
            new Property<>("Object Pivot", this::getPivot, GObject.class)
                    .holds(Vector3.class)
                    .setDescription("The pivot of this object, usually its geometric centre")
                    .setNewValueConsumer(this::setPivot);

    private void addProps() {
        properties.add(
                new Property<>("Object Id", this::getId, GObject.class)
                        .holds(UUID.class)
                        .setDescription("The id of this object").constant()
        );
        properties.add(
                new Property<>("Object Colour", this::getColour, GObject.class)
                        .holds(Color.class)
                        .setDescription("The colour of this object")
                        .setNewValueConsumer(this::setColour)
        );
        properties.add(
                pivotProperty
        );
    }

    /**
     * Returns the pivot point.
     * @implSpec Child-classes need to guarantee that this returns the
     * exact mathematical pivot point of this object. Which if it does
     * not define as some special point, nees to be it's centre.
     * @return a CartesianPoint
     */
    public Vector3 getPivot() {
        return pivot;
    }

    /**
     * Deletes itself
     * @return true if the object was deleted
     * @implNote This is meant to be overriden by inheritors.
     */
    public boolean deleteSelf() {
        this.detachAll();
        return false;
    }

    /**
     * Sets the pivot point.
     * @param pivot The new pivot point.
     */
    public void setPivot(Vector3 pivot) {
        this.pivot = pivot;
    }

    /**
     * Sets the colour
     * @param colour The new colour
     */
    public void setColour(Color colour) {
        col = colour;
    }

    /**
     * Returns this geometry's color
     * @return The Color
     */
    public Color getColour() {
        return col;
    }

    /**
     * Returns this geometry's unique identifier
     * @return The UUID
     */
    @Override
    public UUID getId() {
        return Id;
    }

    /**
     * The name of any GObject is irrelevant and is jsut the UUID.
     * @return The UUID as a String
     */
    @Override
    public String getName() {
        return getId().toString();
    }

    /**
     * Sets this geometry's unique identifier
     * @implSpec This is intended to be used when a child is created from
     * file loading or anything where it hasnt had a UUID attached to it already.
     * Otherwise the UUID is treated as immutable.
     * @param id The new UUID
     * @see ProjectFile#readFile(String, String, Spinner)
     * @see GPoint#fromRaw(String, Vector3)
     * @see GLine#fromRaw(String, GPoint, GPoint)
     * @see GTri#fromRaw(String, Color, GLine, GLine, GLine)
     */
    public void setId(UUID id) {
        Id = id;
    }

    @Override
    public void copy(CopyProperties props) throws InvalidCopyException {
        // implementors override.
    }

    @Override
    public ArrayList<Property<?, ?>> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GObject gObject = (GObject) o;
        return Objects.equals(Id, gObject.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }

    /**
     * @implSpec Child classes are required to override this if they want
     * actual event capabilites. GObject overrides purely to accept the
     * {@link EventListener} contract with no implementation.
     */
    @Override
    public void onEvent(EventType event, EventPayload properties) {

    }

    public boolean isDeletedState() {
        return false;
    }

    public abstract EngineObject.Type getEngineObjectType();

    public EngineObject toObject() {
        return new EngineObject(getEngineObjectType())
                .addProperty(TypeConverter.convertUUID(getId()))
                .addProperty(getPivot().toObject())
                .addProperty(TypeConverter.convertColor(getColour()));
    }

    public EngineObject asReference() {
        return new EngineObject(EngineObject.Type.GREF)
                .addProperty(getEngineObjectType().toString())
                .addProperty(getId().toString());
    }

    // assuming the incoming engine object is a type of GObject
    public static class EngineObjectUtils {
        public static EngineObject getUuid(GettersPack.CallProperties call, EngineObject object) {
            ArrayList<?> arr = TypeConverter.expectArr(object.getProperties().getFirst());
            return TypeConverter.fromArr(arr);
        }
        public static EngineObject getPivot(GettersPack.CallProperties call, EngineObject object) {
            ArrayList<?> arr = TypeConverter.expectArr(object.getProperties().get(1));
            return TypeConverter.fromArr(arr);
        }
        public static EngineObject getColour(GettersPack.CallProperties call, EngineObject object) {
            ArrayList<?> arr = TypeConverter.expectArr(object.getProperties().get(2));
            return TypeConverter.fromArr(arr);
        }
    }
}
