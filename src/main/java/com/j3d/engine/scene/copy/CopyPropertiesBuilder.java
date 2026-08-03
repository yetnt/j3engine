package com.j3d.engine.scene.copy;

import com.j3d.engine.scene.nodes.geometry.GObject;

import java.util.ArrayList;

/**
 * A builder class for creating {@link CopyProperties} instances.
 * This class provides a fluent API to configure various properties
 * related to the copying of {@link GObject} instances.
 * @author Lehlogonolo Poole
 * @see CanCopy
 * @see Copy
 * @see CopyProperties
 * @see InvalidCopyException
 */
public class CopyPropertiesBuilder {
    private CopyProperties properties;

    /**
     * Constructs a new {@code CopyPropertiesBuilder} with the initial
     * list of {@link GObject} instances that are intended to be copied.
     *
     * @param objects The {@link ArrayList} of {@link GObject} instances
     *                for which these copy properties will apply.
     */
    protected CopyPropertiesBuilder(ArrayList<GObject> objects) {
        properties = new CopyProperties(objects);
    }

    /**
     * Sets whether soft dependencies should be considered during the copy operation.
     * Soft dependencies typically refer to objects that are referenced but not
     * necessarily part of the core set of objects being copied.
     *
     * @param sc A boolean value. {@code true} to enable soft dependency handling,
     *           {@code false} to disable it.
     * @return The current {@code CopyPropertiesBuilder} instance for method chaining.
     */
    public CopyPropertiesBuilder softDependencies(boolean sc) {
        properties.setSoftDependencies(sc);
        return this;
    }

    /**
     * Builds and returns the configured {@link CopyProperties} object.
     * This method should be called at the end of the builder chain to
     * obtain the final immutable {@code CopyProperties} instance.
     *
     * @return The fully configured {@link CopyProperties} object.
     */
    public CopyProperties build() {
        return properties;
    }
}
