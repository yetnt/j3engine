package com.j3d.engine.draw.tris;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.gen.settings.Settings;

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

    /**
     * Determines if a triangle should be culled based on back-face culling.
     * @param tri The triangle to be culled.
     * @return {@code true} if the triangle should be culled, {@code false} otherwise.
     * @implNote If {@link Settings#sceneProperties#backFaceCulled(GTri)} is false, this exits early.
     * Also quite buggy TODO Fix this or enforce consistent winding of triangles.
     */
    public boolean backFaceCulled(GTri tri) {
        if (!Settings.sceneProperties.useBackFaceCulling.getValue()) return false;
        Vector3 N = tri.normal;
        Vector3 P = Static.camera.getPosition().sub(tri.getLegA().getStart().getPivot());
        return N.dot(P) < 0;
    }
}
