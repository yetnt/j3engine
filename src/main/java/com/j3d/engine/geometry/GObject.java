package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventListener;
import com.j3d.engine.events.EventType;
import com.j3d.engine.geometry.base.CartesianPoint;

import java.util.*;

/**
 * GObject is an abstract class that represents any actual tangible Geometry in the 2d space (Cartesian space) that a user can see and interact with.
 */
public abstract class GObject extends EventEmitter implements EventListener {
    /**
     * The pivot point of this geometry. Unless a {@link GPoint} where this represents the actual location
     * of the point.
     */
    private CartesianPoint pivot;
    /**
     * A Unique UUID to identify this geometry.
     */
    private final String Id;

    /**
     * Draws this geometry to the screen.
     * @param renderer The Renderer instance
     * @implNote This is meant to be overridden by inheritors.
     */
    public void draw(Renderer renderer) {
        return;
    }

    /**
     * Default Constructor.
     */
    public GObject(Renderer renderer) {
        Id = UUID.randomUUID().toString();
        renderer.gObjects.add(this);
    }

    /**
     * Returns the pivot point.
     * @return a CartesianPoint
     */
    public CartesianPoint getPivot() {
        return pivot;
    }

    /**
     * Deletes itself
     * @return true if the object was deleted
     * @implNote This is meant to be overriden by inheritors.
     */
    public boolean deleteSelf(Renderer renderer) {
        return renderer.delete(this);
    }

    /**
     * Sets the pivot point.
     * @param pivot The new pivot point.
     */
    public void setPivot(CartesianPoint pivot) {
        this.pivot = pivot;
    }

    /**
     * Returns this geometry's unique identifier
     * @return The UUID
     */
    public String getId() {
        return Id;
    }

    /**
     * Converts this Geometry into an Array format that can be used around by Jaiva implementations
     * @return An ArrayList of the ID, and the pivot point in a 2d array.
     */
    public ArrayList<Object> toArray() {
        return new ArrayList<>(Arrays.asList(getId(), getPivot().toArray()));
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

    @Override
    public void onEvent(EventType event, EventBroadcast properties) {

    }
}
