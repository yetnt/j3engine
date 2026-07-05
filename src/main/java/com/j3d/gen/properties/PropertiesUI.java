package com.j3d.gen.properties;

import com.j3d.engine.geometry.geo2d.graphics.*;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.layer.Layer;
import com.j3d.gen.settings.CoreSettings;
import com.j3d.ui.engine.properties.PropertyEntry;
import com.j3d.ui.engine.properties.panels.*;
import com.j3d.utility.generic.HashMultiMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static com.j3d.Static.sceneManager;

public class PropertiesUI {
    public static JPanel getSpecificPanel(ArrayList<Property<?, ?>> list) throws Exception {
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

    public static <T extends HasProperties> ArrayList<JPanel> get(ArrayList<T> objects) {
        ArrayList<Property<?, ?>> properties = switchFilter(objects);
        return minimize(properties);
    }

    private static <T extends HasProperties> ArrayList<Property<?, ?>> switchFilter(ArrayList<T> objects) {
        return switch (CoreSettings.selectionFilter) {
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

    private static <T extends HasProperties>
        boolean commonPropertiesBool(Class<?> provider, Class<?> expected) {
        if (expected == GLine.class || expected == GTri.class || expected == GPoint.class) {
            return provider == expected || provider == GObject.class;
        } else {
            return provider == expected;
        }
    }

    private static ArrayList<JPanel> minimize(ArrayList<Property<?, ?>> properties) {
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

//        if (map.holdsSingletonLists()) {
//            // This is one singular object with its properties. Show values.
//            properties.forEach(
//                    p -> {
//                        try {
//                            panels.add(
//                                    buildPanel(
//                                            getSpecificPanel(new ArrayList<>(List.of(p)))
//                                    )
//                            );
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//            );
//            return panels;
//        } else {
//            // use the maps.
//            // first off, if any of the suppliers converge to the same value, show that value.
//
//        }
    }

    private static JPanel buildPanel(JPanel innerPanel) {
        return new PropertyEntry(innerPanel);
    }

    public static <T extends HasProperties> ArrayList<T> getFilteredObjects(ArrayList<GObject> objects) {
        ArrayList<T> filtered = new ArrayList<>();
        objects.forEach(
                o -> {
                    switch (CoreSettings.selectionFilter) {
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

        return filtered;
    }
}
