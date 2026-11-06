package com.j3d.engine.scene.draw.methods;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.nodes.geometry.GTri;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * CamDistSort is a sorting method that sorts objects based on their distance from the camera.
 * Objects farther from the camera are placed before those closer to the camera.
 * @author Lehlogonolo Poole
 * @see SortMethod
 * @see SceneRenderer
 */
public final class CamDistSort extends SortMethod {

    public CamDistSort() {
        super();
    }

    /**
     * Clears the list and re-adds all non-dirty objects from the registered listeners.
     */
    @Override
    public void clear() {
        super.clear();
        new ArrayList<>(this).stream()
                .peek(s -> {
                    if (s == null)
                        remove(null);
                })
                .filter(Objects::nonNull)
                .filter(t -> !t.isValid()
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
                // fall back to euclideanDist() if depths are equal
                double euclidDist1 = euclideanDist(tri1);
                double euclidDist2 = euclideanDist(tri2);
                return Double.compare(euclidDist2, euclidDist1); // Sort in descending order (farthest first)
        });
    }

    public double euclideanDist(@NotNull RenderState<?, ?> d) {
        return d.getPivot().sub(StaticRefs.getCamera().getPosition()).magnitude();
    }
}
