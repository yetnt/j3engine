package com.j3d.engine.draw.methods;

import com.j3d.engine.draw.TriListener;
import com.j3d.engine.geometry.geo2d.GTri;

import java.util.ArrayList;

public class CamDistSort extends ArrayList<GTri> {
    private static ArrayList<TriListener> registered = new  ArrayList<>();

    public CamDistSort(ArrayList<TriListener> registered) {
        super();
        CamDistSort.registered = registered; // Set the static registered list, since its stored by instance.
    }

    @Override
    public boolean add(GTri gTri) {
        if (this.contains(gTri))
            return false;
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
            TriListener listener1 = registered.stream().filter(
                    listener -> listener.triID.equals(tri1.getId())
            ).findFirst().orElse(null);
            TriListener listener2 = registered.stream().filter(
                    listener -> listener.triID.equals(tri2.getId())
            ).findFirst().orElse(null);
            if (listener1 == null || listener2 == null)
                return 0;
            double dist1 = listener1.lastDistanceFromCamera;
            double dist2 = listener2.lastDistanceFromCamera;
            return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
        });
    }
}
