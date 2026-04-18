package com.j3d.engine.draw.tris.methods;

import com.j3d.engine.draw.tris.SortMethod;
import com.j3d.engine.draw.tris.TriListener;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

import java.util.ArrayList;

/**
 * This is a merge of {@link CamDistSort} and {@link CamDepthSort}.
 * Where it first sorts by depth via {@link GTri#calcDepth()},
 * and then falls back to euclidean distance from the camera if two GTri objects have the same depth
 * and finally falls back to ID comparison to ensure consistent ordering.
 * @author Lehlogonolo Poole
 * @see CamDepthSort
 * @see CamDistSort
 * @see java.util.UUID
 * @see SortMethod
 * @see TriListener
 * @see TriStateArea
 */
public class DDUUIDSort extends SortMethod {

    public DDUUIDSort(ArrayList<TriListener> registered) {
        super(registered);
    }

    @Override
    public boolean add(GTri gTri) {
        if (backFaceCulled(gTri)) return false;
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
            if (Double.compare(dist1, dist2) == 0) {
                // fall back to euclideanDist() if depths are equal
                double euclidDist1 = tri1.getPivot().magnitude();
                double euclidDist2 = tri2.getPivot().magnitude();
                if  (Double.compare(euclidDist1, euclidDist2) == 0) {
                    // final fallback to ID comparison to ensure consistent ordering
                    return tri1.getId().compareTo(tri2.getId());
                }
                return Double.compare(euclidDist2, euclidDist1); // Sort in descending order (farthest first)
            }
            return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
        });
    }
}
