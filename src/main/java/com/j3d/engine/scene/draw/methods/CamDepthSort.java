package com.j3d.engine.scene.draw.methods;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.PureListener;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SortMethod;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.math.matrix.Vector3;

import java.util.ArrayList;

/**
 * CamDepthSort is a sorting method that sorts GTri objects based on their depth values.
 * GTri objects farther from the camera are placed before those closer to the camera.
 * This is calculated by {@link GTri#calcDepth()}.
 * @author Lehlogonolo Poole
 * @see GTri#calcDepth()
 * @see SortMethod
 * @see PureListener
 * @see SceneRenderer
 */
public class CamDepthSort extends SortMethod {

    public CamDepthSort(ArrayList<PureListener> registered) {
        super(registered);
    }

    @Override
    public boolean add(RenderState<?, ?> gTri) {
        if (gTri.getPure() instanceof GTri t)
            if (backFaceCulled(t)) return false;
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
//            PureListener listener1 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri1.getId())
//            ).findFirst().orElse(null);
//            PureListener listener2 = registered.stream().filter(
//                    listener -> listener.triID.equals(tri2.getId())
//            ).findFirst().orElse(null);
//            if (listener1 == null || listener2 == null)
//                return 0;
            double dist1 = calcDepth(tri1);
            double dist2 = calcDepth(tri2);
            return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
        });
    }

    public static double calcDepth(RenderState<?, ?> drawable) {
        Vector3 toTri = drawable.getPivot().sub(StaticRefs.getCamera().getPosition());
        return toTri.dot(StaticRefs.getCamera().getForward().normalize());
    }
}
