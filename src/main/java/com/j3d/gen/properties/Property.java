package com.j3d.gen.properties;

import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.ui.engine.floating.properties.PropertiesPanel;
import com.j3d.ui.engine.floating.properties.PropertyEntry;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Defines a singular property of some object which implements {@link HasProperties}.
 * <p>
 *     While this is intended to have a converter (used by UI), this is designed in a way
 *     to take in generic types. But each type give should follow command parser rules.
 *     hence the following values are allowed:
 * </p>
 * <ul>
 *     <li>{@link Vector3}</li>
 *     <li>{@link UUID}</li>
 *     <li>Any extender of {@link GObject}</li>
 *     <li>{@link Color}</li>
 *     <li>{@link String}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Double}</li>
 *     <li>{@link Boolean}</li>
 * </ul>
 * @param <T> The type of the property (allowed types given above)
 * @param <G> The type of the property provider, this is the class itself which
 *           is defining this property.
 * @see HasProperties
 * @see PropertyKey
 * @see PropertiesUI
 * @see PropertiesPanel
 * @see PropertyEntry
 * @author Lehlogonolo Poole
 */
public class Property<T, G extends HasProperties> {
    /**
     * The property's label
     */
    private final String name;
    /**
     * How the property should be retrieved. A supplier in case it changes.
     */
    private final Supplier<T> valueSupplier;
    /**
     * Whether the value can be edited or not, much like an id
     */
    private boolean constant = false;
    /**
     * The property's description
     */
    private String description;
    /**
     * How to apply a new value to this property.
     */
    private Consumer<T> newValue;
    private JPanel panel;

    /**
     * The class which defined this property. e.g. {@link GTri} may provide it's Vector3 normal,
     * which is provided by itself, but it's id, colour or pivot, are provided by its super class
     * {@link GObject}. This is so when multiple objects are selected, only the properties
     * from the same propertyProvider get shown and can be edited in bulk.
     * @implNote The main use case of this is filtering properties which can be viewed
     * for selections with multiple objects.
     */
    private final Class<G> propertyProvider;

    private Class<T> holds;

    /**
     * Constructs a new Property instance.
     *
     * @param name The display name or label of the property.
     * @param value A {@link Supplier} that provides the current value of the property.
     * @param providerClass The class that "owns" or provides this property.
     */
    public Property(String name, Supplier<T> value, Class<G> providerClass) {
        this.name = name;
        this.valueSupplier = value;
        this.propertyProvider = providerClass;
    }

    public Property<T, G> holds(Class<T> clazz) {
        holds = clazz;
        return this;
    }

    /**
     * Marks this property as constant, meaning its value cannot be changed.
     * This is useful for properties like IDs.
     *
     * @return This Property instance for method chaining.
     */
    public Property<T, G> constant() {
        this.constant = true;
        return this;
    }

    /**
     * Sets a descriptive text for this property.
     *
     * @param description A string explaining what the property represents.
     * @return This Property instance for method chaining.
     */
    public Property<T, G> setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets a {@link Consumer} that will be used to apply new values to this property.
     * If no consumer is set, the property is effectively read-only (unless it's constant, in which case it's always read-only).
     *
     * @param newValue A {@link Consumer} that accepts a new value of type T and applies it.
     * @return This Property instance for method chaining.
     */
    public Property<T, G> setNewValueConsumer(Consumer<T> newValue) {
        this.newValue = newValue;
        return this;
    }

    /**
     * Returns the name (label) of this property.
     *
     * @return The property's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Supplier} responsible for retrieving the current value of this property.
     *
     * @return The value supplier.
     */
    public Supplier<T> getValueSupplier() {
        return valueSupplier;
    }

    /**
     * Checks if this property is marked as constant (uneditable).
     *
     * @return {@code true} if the property is constant, {@code false} otherwise.
     */
    public boolean isConstant() {
        return constant;
    }

    /**
     * Returns the description of this property.
     *
     * @return The property's description, or {@code null} if not set.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the {@link Consumer} used to set new values for this property.
     *
     * @return The new value consumer, or {@code null} if not set.
     */
    public Consumer<T> getNewValueConsumer() {
        return newValue;
    }

    /**
     * Returns the class that is considered the provider or owner of this property.
     *
     * @return The class of the property provider.
     */
    public Class<G> getPropertyProvider() {
        return propertyProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Property<?, ?> property = (Property<?, ?>) o;
        return
                isConstant() == property.isConstant() &&
                        Objects.equals(getName(), property.getName()) &&
                        Objects.equals(getDescription(), property.getDescription()) &&
                        Objects.equals(getPropertyProvider(), property.getPropertyProvider());
    }

    public PropertyKey<T, G> getKey() {
        return new PropertyKey<>(
                name,
                description,
                holds,
                propertyProvider,
                constant
        );
    }

//    public JPanel toPanel() throws Exception {
//        if (panel == null) {
//            panel = PropertiesUI.getSpecificPanel(new ArrayList<>(this));
//        }
//        return panel;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isConstant(), getDescription(), getPropertyProvider());
    }

    public Class<T> getPropertyType() {
        return holds;
    }
}
