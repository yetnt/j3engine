package com.j3d.engine.draw.tris.methods;

import com.j3d.engine.draw.tris.SortMethod;
import com.j3d.engine.draw.tris.TriListener;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.geometry.geo2d.GTri;

import java.util.ArrayList;

/**
 * CamDistSort is a sorting method that sorts GTri objects based on their distance from the camera.
 * GTri objects farther from the camera are placed before those closer to the camera.
 * @author Lehlogonolo Poole
 * @see SortMethod
 * @see TriListener
 * @see TriStateArea
 */
public class CamDistSort extends SortMethod {

    public CamDistSort(ArrayList<TriListener> registered) {
        super(registered);
    }

    @Override
    public boolean add(GTri gTri) {
        if (this.contains(gTri)) {
            sort();
            return false;
        }
        boolean changed = super.add(gTri);
        sort();
        return changed;
    }

    /**
     * Clears the list and re-adds all non-dirty GTri objects from the registered listeners.
     */
    @Override
    public void clear() {
        super.clear();
        registered.stream().filter(
                triListener -> !triListener.isDirty()
        ).forEach(
                listener -> this.add(listener.tri)
        );
    }

    /**
     * Sorts the GTri objects in the list based on their distance from the camera.
     * GTri objects farther from the camera are placed before those closer to the camera.
     */
    private void sort() {
        this.sort((tri1, tri2) -> {
//            TriListener listener1 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri1.getId())
//            ).findFirst().orElse(null);
//            TriListener listener2 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri2.getId())
//            ).findFirst().orElse(null);
//            if (listener1 == null || listener2 == null)
//                return 0;
                // fall back to euclideanDist() if depths are equal
                double euclidDist1 = tri1.euclideanDist();
                double euclidDist2 = tri2.euclideanDist();
                return Double.compare(euclidDist2, euclidDist1); // Sort in descending order (farthest first)
        });
    }
}
