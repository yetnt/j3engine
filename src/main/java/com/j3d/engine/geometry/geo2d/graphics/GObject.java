package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.engine.geometry.geo2d.BaseObject;
import com.j3d.engine.geometry.geo2d.constraints.CObject;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.storage.files.ProjectFile;
import com.j3d.ui.util.Throbber;

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
public abstract class GObject extends EventEmitter implements BaseObject, EventListener {
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
    protected CObject constraintObject;

    /**
     * Draws this geometry to the screen.
     * @param graphics2D The Graphics2D instance
     * @implSpec This is meant to be overridden by child classes.
     */
    public void draw(Graphics2D graphics2D) {
        return;
    }

    /**
     * Draws this geometry to the screen, but in a selected state.
     * @param graphics2D The Graphics2D instance
     * @implSpec This is meant to be overridden by child classes.
     */
    public void drawSelected(Graphics2D graphics2D) {
        return;
    }

    /**
     * Default Constructor.
     */
    public GObject() {
        Id = UUID.randomUUID();
    }


    /**
     * Default Constructor. with color
     * @param colour The colour.
     */
    public GObject(Color colour) {
        Id = UUID.randomUUID();
        col = colour;
    }

    @Override
    public Vector3 getPivot() {
        return pivot;
    }

    @Override
    public boolean deleteSelf() {
        this.detachAll();
        return false;
    }

    public void setPivot(Vector3 pivot) {
        this.pivot = pivot;
        toConstraintObject().setPivot(pivot);
    }

    @Override
    public void setColour(Color colour) {
        col = colour;
        toConstraintObject().setColour(colour);
    }

    @Override
    public Color getColour() {
        return col;
    }

    @Override
    public UUID getId() {
        return Id;
    }

    @Override
    public void setId(UUID id) {
        Id = id;
        toConstraintObject().setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GObject gObject = (GObject) o;
        return Objects.equals(pivot, gObject.pivot) && Objects.equals(Id, gObject.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pivot, Id);
    }

    /**
     * @implSpec Child classes are required to override this if they want
     * actual event capabilites. GObject overrides purely to accept the
     * {@link EventListener} contract with no implementation.
     */
    @Override
    public void onEvent(EventType event, EventPayload properties) {

    }

    public void detachConstraint() {
        constraintObject = null;
    }

    public CObject toConstraintObject() {
        if (constraintObject == null) {
            constraintObject = new CObject(this);
        }
        return constraintObject;
    }
}
