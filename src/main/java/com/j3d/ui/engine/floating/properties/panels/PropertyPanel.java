package com.j3d.ui.engine.floating.properties.panels;

import com.j3d.StaticConfig;
import com.j3d.StaticRefs;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.gen.properties.Property;
import com.j3d.ui.engine.floating.properties.PropertiesPanel;
import com.j3d.ui.engine.floating.properties.PropertyEntry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.j3d.StaticRefs.getPropertiesPanel;
import static com.j3d.engine.SceneManager.history;

/**
 * PropertyPanel is the interface which defines how a {@link PropertyEntry}'s sub panel
 * displays and edits components.
 * <p>
 *     This interface exists such as to allow all sub panels to access the entire list of
 *     {@link Property} associated with it.
 * </p>
 * <p>
 *     The following classes implement this interface:
 *     {@link BooleanProperty}, {@link ColourProperty}, {@link IDProperty}, {@link IntProperty},
 *     {@link ObjectProperty}, {@link StringProperty}, {@link Vector3Property}
 * </p>
 * @param <T> The type that the properties all hold.
 * @see Property
 * @see PropertiesPanel
 * @see PropertyEntry
 * @author Lehlogonolo Poole
 */
public interface PropertyPanel<T> {
    /**
     * Returns a list of properties that this panel is responsible for displaying and editing.
     * @return An {@link ArrayList} of {@link Property} objects.
     */
    ArrayList<Property<T, ?>> getProperties();

    /**
     * Updates the UI fields of the panel to reflect the current values of the properties.
     */
    void setFields();
    /**
     * Converts a list of generic properties to a list of properties of the specific type {@code T}.
     * This method assumes that all properties in the input list are indeed of type {@code T}.
     * @param properties The list of generic properties to convert.
     * @return An {@link ArrayList} of {@link Property} objects of type {@code T}.
     */
    default ArrayList<Property<T, ?>> typeConvert(ArrayList<Property<?, ?>> properties) {
        // before using this method in a panel, its
        // guaranteed its all holding the same type.
        return properties.stream()
                .map(p -> (Property<T, ?>)p)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    /**
     * Checks if this panel is managing a single property or multiple properties.
     * @return {@code true} if the panel manages only one property, {@code false} otherwise.
     */
    default boolean singleProperty() {
        return getProperties().size() == 1;
    }
    /**
     * Returns the single property managed by this panel.
     * @implSpec If {@link #singleProperty()} returns false, which implies there are multiple properties,
     * this returns the first property within the properties list.
     * @return The single {@link Property} object.
     */
    default Property<T, ?> getSingleProperty() {
        return getProperties().getFirst();
    }
    /**
     * Generates a description for an action based on whether it's a single or batch edit,
     * the type of property, and the new value.
     * @param type The type of the property being edited (e.g., "Int", "String").
     * @param val The new value being applied to the property.
     * @return A {@link String} describing the action.
     */
    default String getActionDesc(String type, Object val) {
        return
                (singleProperty() ? "(Single) " : "(Batch) ")
                + type + "PropertyEdit:" + val.toString();
    }
    /**
     * Executes an action that modifies the properties and adds it to the history for undo/redo functionality.
     * This method handles both single and batch property edits.
     * @param value The new value to be set for the properties.
     * @param type The type of the property being edited (e.g., "Int", "String").
     */
    default void runAndAddAction(T value, String type) {
        final LocalTime now = LocalTime.now();
        VoidAction action = new VoidAction() {
            final ArrayList<T> oldValues = getProperties()
                    .stream()
                    .map(Property::getValueSupplier)
                    .map(Supplier::get)
                    .collect(Collectors.toCollection(ArrayList::new));

            @Override
            public Void run() {
                getProperties()
                        .forEach(p -> p.getNewValueConsumer().accept(value));
                setFields();
                getPropertiesPanel().repaint();
                return null;
            }

            @Override
            public void undo() {
                for (int i = 0; i < getProperties().size(); i++) {
                    Property<T, ?> property = getProperties().get(i);
                    T value = oldValues.get(i);
                    property.getNewValueConsumer().accept(value);
                }
                setFields();
                getPropertiesPanel().repaint();
            }

            @Override
            public boolean isReversible() {
                return true;
            }

            @Override
            public String getDescription() {
                return getActionDesc(type, value);
            }

            @Override
            public LocalTime getTime() {
                return now;
            }
        };
        action.run();
        history.add(action);
        StaticRefs.getMainPanel().repaint();
    }
}
