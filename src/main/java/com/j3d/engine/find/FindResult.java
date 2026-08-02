package com.j3d.engine.find;

import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.layer.Layer;

/**
 * A container class for the results of a search operation performed by the {@link Finder}.
 * It encapsulates a found {@link Layer}, {@link Thing}, or {@link GObject}, along with their
 * hierarchical indices, providing a structured way to return search outcomes.
 * An empty {@code FindResult} indicates that no object matching the query was found.
 * @see Finder
 * @see Query
 * @see Layer
 * @see Thing
 * @see GObject
 * @author Lehlogonolo Poole
 */
public class FindResult {
    private Layer layer;
    private int layerIndex = -1;
    private Thing thing;
    private int thingIndex = -1;
    private GObject gObject;
    private int gObjectIndex = -1;
    private boolean empty = false;

    protected FindResult() {
        empty = true;
    }
    protected FindResult(Layer l, int i) {
        layer = l;
        layerIndex = i;
    }
    protected FindResult(Layer l, int i, Thing t, int f) {
        layer = l;
        layerIndex = i;
        thing = t;
        thingIndex = f;
    }
    protected FindResult(Layer l, int i, Thing t, int f, GObject g, int j) {
        layer = l;
        layerIndex = i;
        thing = t;
        thingIndex = f;
        gObject = g;
        gObjectIndex = j;
    }

    /**
     * Checks if this FindResult is empty, meaning it was created without any found objects.
     * @return true if the result is empty, false otherwise.
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Checks if this FindResult contains a Layer.
     * @return true if a Layer is present, false otherwise.
     */
    public boolean containsLayer() {
        return layer != null;
    }

    /**
     * Checks if this FindResult contains a Thing.
     * @return true if a Thing is present, false otherwise.
     */
    public boolean containsThing() {
        return thing != null;
    }

    /**
     * Checks if this FindResult contains a GObject.
     * @return true if a GObject is present, false otherwise.
     */
    public boolean containsGObject() {
        return gObject != null;
    }

    /**
     * Returns the index of the found GObject within its parent Thing.
     * @return The index of the GObject, or -1 if no GObject was found or its index is not set.
     */
    public int getgObjectIndex() {
        return gObjectIndex;
    }

    /**
     * Returns the found GObject.
     * @return The GObject, or null if no GObject was found.
     */
    public GObject getgObject() {
        return gObject;
    }

    /**
     * Returns the index of the found Layer.
     * @return The index of the Layer, or -1 if no Layer was found or its index is not set.
     */
    public int getLayerIndex() {
        return layerIndex;
    }

    /**
     * Returns the found Layer.
     * @return The Layer, or null if no Layer was found.
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Returns the index of the found Thing within its parent Layer.
     * @return The index of the Thing, or -1 if no Thing was found or its index is not set.
     */
    public int getThingIndex() {
        return thingIndex;
    }

    /**
     * Returns the found Thing.
     * @return The Thing, or null if no Thing was found.
     */
    public Thing getThing() {
        return thing;
    }
}