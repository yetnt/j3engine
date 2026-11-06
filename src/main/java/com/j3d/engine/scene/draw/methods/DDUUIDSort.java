package com.j3d.engine.scene.draw.methods;

import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.nodes.geometry.GTri;

import static com.j3d.engine.scene.draw.methods.CamDepthSort.calcDepth;

/**
 * This is a merge of {@link CamDistSort} and {@link CamDepthSort}.
 * Where it first distance then falls back to depth from the camera
 * and finally falls back to ID comparison to ensure consistent ordering.
 * @author Lehlogonolo Poole
 * @see CamDepthSort
 * @see CamDistSort
 * @see java.util.UUID
 * @see SortMethod
 * @see SceneRenderer
 */
public final class DDUUIDSort extends SortMethod {

    public DDUUIDSort() {
        super();
    }

    /**
     * Clears the list and re-adds all non-dirty objects from the registered listeners.
     */
    @Override
    public void clear() {
        super.clear();
        stream().filter(
                t -> !t.isValid()
        ).forEach(
                this::add
        );
    }

    /**
     * Sorts the objects in the list based on their distance from the camera.
     * Objects farther from the camera are placed before those closer to the camera.
     */
    @Override
    public void sort() {
        this.sort((tri1, tri2) -> {
            double euclidDist1 = tri1.getPivot().magnitude();
            double euclidDist2 = tri2.getPivot().magnitude();
            if  (Double.compare(euclidDist1, euclidDist2) == 0) {
                // fall back to depth if distances are equal
                double dist1 = calcDepth(tri1);
                double dist2 = calcDepth(tri2);
                if (Double.compare(dist1, dist2) == 0) {
                    // final fallback to ID comparison to ensure consistent ordering
                    return tri1.getId().compareTo(tri2.getId());
                }
                return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
            }
            return Double.compare(euclidDist2, euclidDist1); // Sort in descending order (farthest first)
        });
    }
}
