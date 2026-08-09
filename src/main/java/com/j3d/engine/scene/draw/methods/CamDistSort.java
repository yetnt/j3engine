package com.j3d.engine.scene.draw.methods;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.draw.SortMethod;
import com.j3d.engine.scene.nodes.geometry.GTri;

import java.util.ArrayList;
import java.util.Objects;

/**
 * CamDistSort is a sorting method that sorts GTri objects based on their distance from the camera.
 * GTri objects farther from the camera are placed before those closer to the camera.
 * @author Lehlogonolo Poole
 * @see SortMethod
 * @see SceneRenderer
 */
public class CamDistSort extends SortMethod {

    public CamDistSort() {
        super();
    }

    @Override
    public boolean add(RenderState<?, ?> gTri) {
        if (gTri.getPure() instanceof GTri t)
            if (backFaceCulled(t)) return false;
        if (this.contains(gTri)) {
//            sort();
            return false;
        }
        //        sort();
        return super.add(gTri);
    }

    /**
     * Clears the list and re-adds all non-dirty GTri objects from the registered listeners.
     */
    @Override
    public void clear() {
        super.clear();
        new ArrayList<>(this).stream()
                .peek(s -> {
                    if (s == null)
                        remove(s);
                })
                .filter(Objects::nonNull)
                .filter(t -> !t.isValid()
        ).forEach(
                        this::add
        );
    }

    /**
     * Sorts the GTri objects in the list based on their distance from the camera.
     * GTri objects farther from the camera are placed before those closer to the camera.
     */
    @Override
    public void sort() {
        this.sort((tri1, tri2) -> {
//            PureListener listener1 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri1.getId())
//            ).findFirst().orElse(null);
//            PureListener listener2 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri2.getId())
//            ).findFirst().orElse(null);
//            if (listener1 == null || listener2 == null)
//                return 0;
                // fall back to euclideanDist() if depths are equal
                double euclidDist1 = euclideanDist(tri1);
                double euclidDist2 = euclideanDist(tri2);
                return Double.compare(euclidDist2, euclidDist1); // Sort in descending order (farthest first)
        });
    }

    public double euclideanDist(RenderState<?, ?> d) {
        return d.getPivot().sub(StaticRefs.getCamera().getPosition()).magnitude();
    }
}
