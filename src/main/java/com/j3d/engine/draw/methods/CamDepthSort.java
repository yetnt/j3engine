package com.j3d.engine.draw.methods;

import com.j3d.engine.draw.SortMethod;
import com.j3d.engine.draw.TriListener;
import com.j3d.engine.geometry.geo2d.GTri;

import java.util.ArrayList;

/**
 * CamDepthSort is a sorting method that sorts GTri objects based on their depth values.
 * GTri objects farther from the camera are placed before those closer to the camera.
 * This is calculated by {@link GTri#calcDepth()}.
 */
public class CamDepthSort extends SortMethod {

    public CamDepthSort(ArrayList<TriListener> registered) {
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
            double dist1 = tri1.calcDepth();
            double dist2 = tri2.calcDepth();
            return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
        });
    }
}
