package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventListener;
import com.j3d.engine.events.EventType;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;
import java.util.*;

/**
 * GObject is an abstract class that represents any actual tangible Geometry in the 2d space (Cartesian space) that a user can see and interact with.
 */
public abstract class GObject extends EventEmitter implements EventListener {
    protected Color col = Color.BLACK;
    /**
     * The pivot point of this geometry. Unless a {@link GPoint} where this represents the actual location
     * of the point.
     */
    private Vector3 pivot;
    /**
     * A Unique UUID to identify this geometry.
     */
    private final String Id;

    /**
     * Draws this geometry to the screen.
     * @param renderer The Renderer instance
     * @param graphics2D The Graphics2D instance
     * @param cam The camera.
     * @implNote This is meant to be overridden by inheritors.
     */
    public void draw(Renderer renderer, Graphics2D graphics2D, Camera cam) {
        return;
    }

    /**
     * Default Constructor.
     */
    public GObject() {
        Id = UUID.randomUUID().toString();
    }


    /**
     * Default Constructor. with color
     * @param colour The colour.
     */
    public GObject(Color colour) {
        Id = UUID.randomUUID().toString();
        col = colour;
    }

    /**
     * Returns the pivot point.
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
    public boolean deleteSelf(Renderer renderer, GObject ...excluded) {
        return renderer.delete(this);
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
