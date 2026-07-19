package com.j3d.gen.properties;

import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.graphics.*;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.engine.floating.properties.PropertyEntry;
import com.j3d.ui.engine.floating.properties.panels.*;
import com.j3d.utility.generic.HashMultiMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static com.j3d.StaticRefs.getSceneManager();

public class PropertiesUI {

    public static SelectionPropertiesFilter selectionPropertiesFilter = SelectionPropertiesFilter.DEFAULT;

    /**
     * Retrieves and merges properties from a list of objects that implement {@link HasProperties}.
     * The properties are filtered based on the current {@link PropertiesUI#selectionPropertiesFilter}.
     *
     * @param objects An ArrayList of objects implementing {@link HasProperties}.
     * @param <T> The type of objects, which must extend {@link HasProperties}.
     * @return An ArrayList of JPanels, each representing a merged property.
     */
    public static <T extends HasProperties> ArrayList<JPanel> get(ArrayList<T> objects) {
        ArrayList<Property<?, ?>> properties = switchFilter(objects);
        ArrayList<JPanel> panels = merge(properties);
        // sort the panels so it's always consistent.
        panels.sort(
                (o1, o2) -> {
                    PropertyEntry e1 = (PropertyEntry) o1;
                    PropertyEntry e2 = (PropertyEntry) o2;
                    return e1.label.compareTo(e2.label);
                }
        );
        return panels;
    }

    /**
     * Filters a list of {@link GObject}s based on the current {@link PropertiesUI#selectionPropertiesFilter}.
     * @param objects The ArrayList of {@link GObject}s to filter.
     * @param <T> The type of objects to filter for, which must extend {@link HasProperties}.
     * @return A HashSet of filtered objects of type T.
     * @implSpec This is the method which should be called on {@link SceneManager#getSelected()}
     * before being passed into {@link #get(ArrayList)} such that stuff like Thing and layer can participate
     * and that there aren't any duplicate objects.
     */
    public static <T extends HasProperties> ArrayList<T> getFilteredObjects(ArrayList<GObject> objects) {
        HashSet<T> filtered = new HashSet<>();
        objects.forEach(
                o -> {
                    switch (selectionPropertiesFilter) {
                        case DEFAULT -> filtered.add((T) o);
                        case LINE -> {
                            if (o instanceof GLine) filtered.add((T) o);
                        }
                        case TRI -> {
                            if (o instanceof GTri) filtered.add((T) o);
                        }
                        case POINT -> {
                            if (o instanceof GPoint) filtered.add((T) o);
                        }
                        case THING -> filtered.add(
                                (T) sceneManager.findObjectParent(o)
                        );
                        case LAYER -> filtered.add(
                                (T) sceneManager.findThingLayer(sceneManager.findObjectParent(o))
                        );
                    }
                }
        );

        return new ArrayList<>(filtered);
    }

    /**
     * Returns a specific JPanel for displaying and editing properties of a given type.
     * @param list An ArrayList of Property objects of the same type.
     * @return A JPanel tailored to the property type.
     * @throws Exception If no specific panel is found for the given property type.
     */
    private static JPanel getSpecificPanel(ArrayList<Property<?, ?>> list) throws Exception {
        Class<?> expected = list.getFirst().getPropertyType();
        if (expected == Integer.class) {
            return new IntProperty(list);
        } else if (expected == String.class) {
            return new StringProperty(list);
        } else if (GObject.class.isAssignableFrom(expected)) { // Check if expected is GObject or a subclass
            return new ObjectProperty<>(list);
        } else if (expected == Vector3.class) {
            return new Vector3Property(list);
        } else if (expected == UUID.class) {
            return new IDProperty(list);
        } else if (expected == Boolean.class) {
            return new BooleanProperty(list);
        } else if (expected == Color.class) {
            return new ColourProperty(list);
        } else {
            throw new Exception("Should not happen. No specific panel for type: " + expected.getName());
        }
    }

    /**
     * Filters properties based on the {@link PropertiesUI#selectionPropertiesFilter}.
     */
    private static <T extends HasProperties> ArrayList<Property<?, ?>> switchFilter(ArrayList<T> objects) {
        return switch (selectionPropertiesFilter) {
            case DEFAULT -> {
                // if all objects are one specific type, say GPoint, GLine or GTri, filter by that.
                // otherwise filter by GObject
                Class<?> commonType = null;
                boolean allSameType = true;

                if (!objects.isEmpty()) {
                    commonType = objects.getFirst().getClass();
                    for (int i = 1; i < objects.size(); i++) {
                        if (commonType != objects.get(i).getClass()) {
                            allSameType = false;
                            break;
                        }
                    }
                }

                if (allSameType && commonType != null) {
                    yield filterCommonProperties(objects, commonType);
                } else {
                    yield filterCommonProperties(objects, GObject.class);
                }

            }
            case POINT -> filterCommonProperties(objects, GPoint.class);
            case LINE -> filterCommonProperties(objects, GLine.class);
            case TRI -> filterCommonProperties(objects, GTri.class);
            case THING -> filterCommonProperties(objects, Thing.class);
            case LAYER -> filterCommonProperties(objects, Layer.class);
        };
    }

    /**
     * Filters properties from a list of objects, keeping only those that are common to the specified class.
     * @param objects The list of objects to filter properties from.
     * @param clazz The class to filter properties by.
     * @return An ArrayList of common properties.
     */
    private static <T extends HasProperties> ArrayList<Property<?, ?>> filterCommonProperties(ArrayList<T> objects, Class<?> clazz) {
        ArrayList<Property<?, ?>> properties = new ArrayList<>();
        for (HasProperties object : objects) {
            for (Property<?, ?> property : object.getProperties()) {
                if (commonPropertiesBool(property.getPropertyProvider(), clazz)) {
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    /**
     * Determines if a property provider class is considered common with an expected class.
     * @param provider The class that provides the property.
     * @param expected The expected class for common properties.
     * @return True if the provider is considered common, false otherwise.
     */
    private static <T extends HasProperties>
        boolean commonPropertiesBool(Class<?> provider, Class<?> expected) {
        if (expected == GLine.class || expected == GTri.class || expected == GPoint.class) {
            return provider == expected || provider == GObject.class;
        } else {
            return provider == expected;
        }
    }

    /**
     * Merges a list of properties into a list of JPanels. Properties with the same key are grouped together.
     * @param properties The ArrayList of properties to merge.
     * @return An ArrayList of JPanels, each representing a merged property.
     */
    private static ArrayList<JPanel> merge(ArrayList<Property<?, ?>> properties) {
        ArrayList<JPanel> panels = new ArrayList<>();
        HashMultiMap<PropertyKey<?, ?>, Property<?, ?>> map = new HashMultiMap<>();
        properties.forEach(
                p -> map.putValue(p.getKey(), p)
        );

        map.forEach((key, value) -> {
            try {
                panels.add(
                        buildPanel(
                                getSpecificPanel(value)
                        )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return panels;
    }

    /**
     * Builds a {@link PropertyEntry} panel around an inner JPanel.
     * @param innerPanel The JPanel to be wrapped.
     * @return A {@link PropertyEntry} containing the inner panel.
     */
    private static PropertyEntry buildPanel(JPanel innerPanel) {
        PropertyEntry propertyEntry = new PropertyEntry(innerPanel);
        propertyEntry.revalidate();
        return propertyEntry;
    }
}
