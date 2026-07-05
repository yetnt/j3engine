package com.j3d.ui.engine.properties.panels;

import com.j3d.gen.properties.Property;

import java.util.ArrayList;
import java.util.stream.Collectors;

public interface PropertyPanel<T> {
    ArrayList<Property<T, ?>> getProperties();
    void setFields();

    default ArrayList<Property<T, ?>> typeConvert(ArrayList<Property<?, ?>> properties) {
        // before using this method in a panel, its
        // guaranteed its all holding the same type.
        return properties.stream()
                .map(p -> (Property<T, ?>)p)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default boolean singleProperty() {
        return getProperties().size() == 1;
    }

    default Property<T, ?> getSingleProperty() {
        return getProperties().getFirst();
    }

    default String getActionDesc(String type, Object val) {
        return
                singleProperty() ? "(Single) " : "(Batch) "
                + type + "PropertyEdit:" + val;
    }
}
