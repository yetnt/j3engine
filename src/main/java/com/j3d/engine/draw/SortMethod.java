package com.j3d.engine.draw;

import com.j3d.engine.geometry.geo2d.GTri;

import java.util.ArrayList;

/**
 * SortMethod is an abstract class which extends ArrayList of GTri and represents
 * a method for sorting triangles.
 */
public abstract class SortMethod extends ArrayList<GTri> {
    /**
     * A static list of registered TriListener objects.
     */
    protected static ArrayList<TriListener> registered = new  ArrayList<>();

    /**
     * Constructor for SortMethod
     *
     * @param registered The list of TriListener objects to be registered.
     */
    public SortMethod(ArrayList<TriListener> registered) {
        super();
        SortMethod.registered = registered; // Set the static registered list, since its stored by instance.
    }
}
