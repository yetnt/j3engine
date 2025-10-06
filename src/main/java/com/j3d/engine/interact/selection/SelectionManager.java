package com.j3d.engine.interact.selection;

import com.j3d.engine.Layer;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.util.ArrayList;
import java.util.function.Predicate;

import static com.j3d.Main.camera;

/**
 * Manages the selection of GObjects within a collection of Layers and Things.
 * Provides functionality to filter and retrieve selected objects based on various criteria.
 * <p>
 *     The renderer should hold one instance of this class to manage the current selection state.
 *     The renderer can also create a new instance of this class, but only for
 *     {@link SelectionType#INCLUDE} where other objects are added to the existing selection or
 *     {@link SelectionType#EXCLUDE} where objects are removed from the existing selection.
 * </p>
 */
public class SelectionManager {
    private ArrayList<GObject> selected = new ArrayList<>();

    /**
     * Constructs a SelectionManager and initializes the selection based on the provided layers and a Selection object.
     * @param objects An ArrayList of Layer objects to iterate through for potential selections.
     * @param selectionQuery A Selection object defining the criteria for initial selection.
     */
    public SelectionManager(ArrayList<Layer> objects, SelectionQuery selectionQuery) {
        for (Layer layer : objects) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects()) {
                    switch (selectionQuery.type) {
                        case BOUNDS_STRICT:
                            // If the entire object is within the selection, it is considered selected.
                            if (selectionQuery.has(obj, false)) selected.add(obj);
                            break;
                        case BOUNDS_SOFT:
                            // If any part of the object is within the selection, it is considered selected.
                            if (selectionQuery.has(obj, true)) selected.add(obj);
                            break;
                        case INVERT:
                            // Inverts the selection. Such as if objects A and B exist, and you select A. Inverting the selection will
                            // deselect A and select B.
                            if (!selectionQuery.has(obj, true)) selected.add(obj);
                            break;
                        case ALL:
                        case EXCLUDE:
                        case INCLUDE:
                        case null, default:
                        {
                            // Due to creating a new selection via this constructor, all the above just return whatever we got.
                            selected.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        // The following filter is supposed to sort by Z but is wonky.
        // Via commands the user will sort what they want and don't want themselves.
//        this.filter(obj -> {
//            Vector3 viewDir = camera.getForward().normalize(); // camera's viewing direction
//
//            if (obj instanceof GTri tri) {
//                Vector3 a = tri.getLegA().getStart().getPivot();
//                Vector3 b = tri.getLegB().getStart().getPivot();
//                Vector3 c = tri.getLegC().getStart().getPivot();
//
//                Vector3 ab = b.sub(a);
//                Vector3 ac = c.sub(a);
//                Vector3 normal = ab.cross(ac).normalize();
//
//                // Check if triangle is facing the camera
//                return normal.dot(viewDir) < 0;
//            }
//
//            if (obj instanceof GLine line) {
//                Vector3 toLine = line.getPivot().sub(camera.getPosition()).normalize();
//                return viewDir.dot(toLine) > 0;
//            }
//
//            if (obj instanceof GPoint point) {
//                Vector3 toPoint = point.getPivot().sub(camera.getPosition()).normalize();
//                return viewDir.dot(toPoint) > 0;
//            }
//
//            return true;
//        });
//
//        System.out.println("check");
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
    public ArrayList<GObject> getSelected() {
        return selected;
    }
}
