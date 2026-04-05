package com.j3d.engine.draw.tris;

import com.j3d.engine.geometry.geo2d.GTri;

import java.util.ArrayList;

/**
 * SortMethod is an abstract class which extends ArrayList of GTri and represents
 * a method for sorting triangles.
 * @implSpec What a sort method should do is, when a triangle is added via {@link #add(Object)},
 * it should sort the list according to its own sorting algorithm.
 * It should also probably override {@link #clear()} to re-add all non-dirty triangles from the registered listeners but
 * that's up to the implementation.
 * @author Lehlogonolo Poole
 * @see ArrayList
 * @see GTri
 * @see TriListener
 * @see TriStateArea
 */
public abstract class SortMethod extends ArrayList<GTri> {
    /**
     * A static list of registered TriListener objects.
     */
    protected final ArrayList<TriListener> registered;

    /**
     * Constructor for SortMethod
     *
     * @param registered The list of TriListener objects to be registered.
     */
    public SortMethod(ArrayList<TriListener> registered) {
        super();
        this.registered = registered;
    }
}
