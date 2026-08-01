package com.j3d.engine.interact.selection;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

import static com.j3d.StaticRefs.getSceneManager;

/**
 * Manages the selection of GObjects within a collection of Layers and Things.
 * Provides functionality to filter and retrieve selected objects based on various criteria.
 * @implSpec
 *     The {@link SceneManager} should hold one instance of this class to manage the current selection state.
 *     The sceneManager can also create a new instance of this class, but only for
 *     {@link SelectionType#INCLUDE} where other objects are added to the existing selection or
 *     {@link SelectionType#EXCLUDE} where objects are removed from the existing selection.
 * @see SelectionMouseOwner
 * @see SelectionUI
 * @see SelectionUtils
 * @see SelectionQuery
 * @see SelectionType
 * @see SceneManager
 * @author Lehlogonolo Poole
 */
public class SelectionManager {
    private final HashSet<GObject> selected = new HashSet<>();
    public static SelectionMouseOwner selectionMouseOwner = new SelectionMouseOwner();

    /**
     * Constructs a SelectionManager and initializes the selection based on the provided layers and a Selection object.
     * @param objects An ArrayList of Layer objects to iterate through for potential selections.
     * @param selectionQuery A Selection object defining the criteria for initial selection.
     */
    public SelectionManager(ArrayList<Layer> objects, SelectionQuery selectionQuery) {
        for (Layer layer : objects) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects()) {
                    HashSet<GObject> objectsToSelect = new HashSet<>();
                    switch (selectionQuery.type) {
                        case BOUNDS_STRICT -> {
                            // If the entire object is within the selection, it is considered selected.
                            if (selectionQuery.has(obj, false)) selected.add(obj);
                        }
                        case BOUNDS_SOFT -> {
                            // If any part of the object is within the selection, it is considered selected.
                            if (selectionQuery.has(obj, true)) {
                                selected.add(obj);
                                if (obj instanceof GLine l) {
                                    objectsToSelect.add(l.getA());
                                    objectsToSelect.add(l.getB());
                                } else if (obj instanceof GTri tri) {
                                    tri.getLegStream().forEach(objectsToSelect::add);
                                    tri.getWinding().stream().forEach(objectsToSelect::add);
                                }
                            }
                        }
                        case SUBTRACT -> {
                            boolean wasSelected = getSceneManager().getSelected().contains(obj);
                            boolean inBox = selectionQuery.has(obj, true);

                            if (wasSelected && !inBox)
                                selected.add(obj); // keep
                        }
                        case UNION -> {
                            // pre-add the current selected objects.
                            getSceneManager().getSelected().stream()
                                    .filter(o -> !selected.contains(o))
                                    .forEach(selected::add);
                            boolean wasSelected = getSceneManager().getSelected().contains(obj);
                            boolean inBox = selectionQuery.has(obj, false);

                            if (!wasSelected && inBox)
                                selected.add(obj); // add
                        }
                        case ALL, EXCLUDE, INCLUDE -> // Due to creating a new selection via this constructor, all the above just return whatever we got.
                                selected.add(obj);
                    }
                    selected.addAll(objectsToSelect);
                    objectsToSelect.clear();
                }
            }
        }

    }

    /**
     * Constructs an empty SelectionManager with no selected GObjects.
     */
    public SelectionManager() {
        // Empty constructor for creating an empty selection manager.
    }

    /**
     * Checks if no GObjects are currently selected.
     *
     * @return true if no GObjects are selected, false otherwise.
     */
    public boolean isNothingSelected() {
        return selected.isEmpty();
    }

    /**
     * Includes the selected GObjects from another SelectionManager into this one.
     * <p>
     *     Typically used when the SelectionType is {@link SelectionType#INCLUDE}.
     * </p>
     * @param other The other SelectionManager whose selected GObjects are to be included.
     * @return This SelectionManager instance, allowing for method chaining.
     */
    public SelectionManager include(SelectionManager other) {
        for (GObject obj : other.getSelected()) {
            if (!selected.contains(obj)) selected.add(obj);
        }
        return this;
    }

    /**
     * Excludes the selected GObjects from another SelectionManager from this one.
     * <p>
     *     Typically used when the SelectionType is {@link SelectionType#EXCLUDE}.
     * </p>
     * @param other The other SelectionManager whose selected GObjects are to be excluded.
     * @return This SelectionManager instance, allowing for method chaining.
     */
    public SelectionManager exclude(SelectionManager other) {
        for (GObject obj : other.getSelected()) {
            selected.remove(obj);
        }
        return this;
    }

    /**
     * Filters the currently selected GObjects based on a given predicate.
     *
     * @param predicate The predicate to apply for filtering. Objects for which the predicate returns true will be removed.
     * @return This SelectionManager instance, allowing for method chaining.
     */
    public SelectionManager filter(Predicate<GObject> predicate) {
        selected.removeIf(predicate);
        return this;
    }

    /**
     * Returns the currently selected GObjects.
     *
     * @return An ArrayList of GObjects that are currently selected.
     */
    public HashSet<GObject> getSelected() {
        return selected;
    }

    public void clear() {
        selected.clear();
    }
}
