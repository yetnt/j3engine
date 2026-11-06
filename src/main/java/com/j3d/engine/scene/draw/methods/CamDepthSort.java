package com.j3d.engine.scene.draw.methods;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * CamDepthSort is a sorting method that sorts objects based on their depth values.
 * Objects farther from the camera are placed before those closer to the camera.
 * @author Lehlogonolo Poole
 * @see SortMethod
 * @see SceneRenderer
 */
public final class CamDepthSort extends SortMethod {

    public CamDepthSort() {
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
            double dist1 = calcDepth(tri1);
            double dist2 = calcDepth(tri2);
            return Double.compare(dist2, dist1); // Sort in descending order (farthest first)
        });
    }

    public static double calcDepth(@NotNull RenderState<?, ?> drawable) {
        Vector3 toTri = drawable.getPivot().sub(StaticRefs.getCamera().getPosition());
        return toTri.dot(StaticRefs.getCamera().getForward().normalize());
    }
}
