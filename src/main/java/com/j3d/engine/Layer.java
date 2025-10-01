package com.j3d.engine;

import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Thing;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * A {@code Layer} is a fundamental concept in the rendering pipeline, representing a
 * collection of {@link GObject} instances that are rendered together. The
 * {@link Renderer} processes these layers in a specific order, drawing the
 * contents of each layer to the screen. By organizing {@code GObject}s into
 * layers, you can control their stacking order and visibility.
 * <p>
 * The {@code Layer} class extends {@link ArrayDeque}, providing a versatile and
 * efficient way to manage the objects within it. You can add, remove, and
 * reorder objects in a layer to dynamically change the scene's composition.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>
 *         <b>Drawing Order:</b> Layers are rendered sequentially. The
 *         {@link Renderer} draws layers one by one, so the order in which you
 *         add layers to the renderer determines which objects appear on top of
 *         others.
 *     </li>
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
public class Layer extends ArrayList<Thing> {

    private final String identifier;

    public static final String backgroundId = "BACKG";

    /**
     * Default Constructor
     * @param id The identifier of the layer.
     */
    public Layer(String id) {
        identifier = id;
    }

    /**
     * Default Constructor. This constructor should not be used by a user as this instantiates the default layer.
     */
    public Layer() {
        identifier = "LAYER-0";
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
     * @return This {@code Layer} after the merge operation, allowing for method chaining.
     */
    public Layer squashWith(Layer otherlayer) {
        this.addAll(otherlayer);
        return this;
    }

    /**
     * Draws all {@link GObject}s contained within this layer onto the provided
     * {@link Graphics2D} context. Each object's {@code draw} method is called.
     *
     * @param renderer The {@code Renderer} instance.
     * @param graphics2D The {@code Graphics2D} context to draw upon.
     * @param camera The {@code Camera} instance.
     */
    public void draw(Renderer renderer,Graphics2D graphics2D, Camera camera) {
        if (!getIdentifier().equals(backgroundId))
            sort(Comparator.comparingDouble(t -> t.getCentroid().distance(camera.getPosition())));
        for (Thing o : this.reversed()) {
            o.draw(renderer, graphics2D, camera);
        }
    }

    @Override
    public String toString() {
        return identifier;
    }
}
