package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The properties used when a copy operation is applied. (When calling {@link GObject#copy(CopyProperties)}
 * <p>
 *     A callee, gets an existing list of {@link GObject} to copy, and creates the CopyOperation.
 *     Then after the callee needs to call {@link GObject#copy(CopyProperties)} on every single object
 *     on the list in order of points, lines and tris. (or else tis thing finna break.)
 * </p>
 * @implSpec Call {@link #builder(ArrayList)} to build a new copy operation to pass into the objects.
 * @author Lehlogonolo Poole
 * @see Copy
 * @see CanCopy
 * @see CopyPropertiesBuilder
 * @see InvalidCopyException
 * @see com.j3d.engine.interact.cmd.commands.copyPaste.CopyCmd
 * @see com.j3d.engine.interact.cmd.commands.copyPaste.PasteCmd
 */
public class CopyProperties {
    private boolean softDependencies = false;
    private final HashSet<Copy> copies = new HashSet<>();
    private final ArrayList<GObject> objects;

    protected CopyProperties(ArrayList<GObject> objects) {
        this.objects = objects;
    }

    /**
     * Sets the dependency handling mode for this copy operation.
     * If soft dependencies are enabled, missing dependencies will be created via a supplier.
     * If disabled, an {@link InvalidCopyException} will be thrown for missing dependencies.
     *
     * @param severeConnections If {@code true}, enables soft dependency handling. If {@code false}, disables it.
     */
    protected void setSoftDependencies(boolean severeConnections) {
        this.softDependencies = severeConnections;
    }

    /**
     * Checks if soft dependency handling is currently enabled.
     *
     * @return {@code true} if soft dependencies are allowed, {@code false} otherwise.
     */
    public boolean allowsSoftDependencies() {
        return softDependencies;
    }

    /**
     * Adds a new original-copy pair to the collection of managed copies.
     *
     * @param original The {@link UUID} of the original {@link GObject}.
     * @param copy The {@link GObject} instance that is a copy of the original.
     */
    public void add(UUID original, GObject copy) {
        copies.add(new Copy(original, copy));
    }

    /**
     * Retrieves the copied {@link GObject} associated with a given original {@link UUID}.
     *
     * @param original The {@link UUID} of the original {@link GObject} to look for.
     * @return The copied {@link GObject} if found, or {@code null} if no copy for the given original {@link UUID} exists.
     */
    public GObject get(UUID original) {
        return copies.stream()
                .filter(c -> c.is(original))
                .findAny()
                .map(Copy::copy)
                .orElse(null);
    }

    /**
     * Checks if a copy for a given original {@link UUID} already exists in the collection.
     *
     * @param original The {@link UUID} of the original {@link GObject} to check for.
     * @return {@code true} if a copy exists for the original {@link UUID}, {@code false} otherwise.
     */
    public boolean exists(UUID original) {
        return copies.stream().anyMatch(c -> c.is(original));
    }

    /**
     * Returns the internal {@link HashSet} of {@link Copy} objects managed by this {@code CopyProperties} instance.
     * Each {@link Copy} object represents an original-copy pair.
     * @return A {@link HashSet} containing all the {@link Copy} objects.
     */
    public HashSet<Copy> getCopies() {
        return copies;
    }

    /**
     * Returns an {@link ArrayList} containing all the copied {@link GObject} instances
     * currently managed by this {@code CopyProperties} object.
     * Each element in the list is the 'copy' part of an original-copy pair.
     *
     * @return An {@link ArrayList} of {@link GObject} instances that are copies.
     */
    public ArrayList<GObject> getCopiesAsObjects() {
        return
                copies.stream()
                .map(Copy::copy)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Creates a new {@link CopyPropertiesBuilder} instance for constructing {@link CopyProperties}.
     *
     * @param objects The initial list of {@link GObject} instances to be managed by the {@link CopyProperties}.
     * @return A new {@link CopyPropertiesBuilder} instance.
     */
    public static CopyPropertiesBuilder builder(ArrayList<GObject> objects) {
        return new CopyPropertiesBuilder(objects);
    }

    /**
     * Checks if a copy for the given {@link UUID} already exists. If it does, returns the existing copy.
     * If not, and soft dependencies are enabled, it creates a new object using the provided supplier,
     * adds it as a copy, and returns it.
     * If not, and soft dependencies are disabled, it throws an {@link InvalidCopyException}.
     *
     * @param id The {@link UUID} of the original object to check for.
     * @param supplier A {@link Supplier} to create a new object of type {@code T} if it doesn't exist
     *                 and soft dependencies are allowed.
     * @param <T> The type of the object, which must extend {@link CanCopy}.
     * @return An existing copy or a newly created and added copy.
     * @throws InvalidCopyException If the object with the given ID is not found and soft dependencies are disabled.
     *                              This exception is handled and re-thrown by {@link StaticRefs#getErrs()}.
     * @see #setSoftDependencies(boolean)
     * @see #allowsSoftDependencies()
     */
    @SuppressWarnings("unchecked")
    public <T extends CanCopy> T existsOrElse(UUID id, Supplier<T> supplier) {
        if (exists(id))  {
            return (T)
                    copies.stream()
                            .filter(c -> c.is(id))
                            .findAny().get()
                            .copy();
        } else {
            if (!softDependencies) StaticRefs.getErrs().handleThenThrow(
                    new InvalidCopyException(
                            "Missing object dependency " + id + " which is not part of the copied selection"
                    ).code(101)
            );
            T t = supplier.get();
            add(id, (GObject) t);
            return t;
        }
    }
}
