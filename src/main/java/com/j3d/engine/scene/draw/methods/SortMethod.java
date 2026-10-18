package com.j3d.engine.scene.draw.methods;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
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
 * @see SceneRenderer
 */
public abstract sealed class SortMethod extends ArrayList<RenderState<?, ?>> 
        permits CamDepthSort, CamDistSort, DDUUIDSort {

    public SortMethod() {
        super();
    }

    @Override
    public boolean add(RenderState<?, ?> drawable) {
        SortMethod sm = this;
        if (this.contains(drawable)) {
            return false;
        } 
        if (drawable.getPure() instanceof Triangle triangle) {
            if (backFaceCulled(triangle)) return false;
        }
//        else if (drawable instanceof DecomposeWhenDrawn<?> d) {
//            d.getDecomposeList().forEach(r-> {
//                if (r.isValid())
//                    sm.add(drawable);
//                else {
//                    StaticRefs.throwIfNull(StaticRefs.getSceneManager()).getRenderer().unregister(drawable);
//                    sm.remove(d);
//                }
//            });
//            return true;
//        } else {
            return super.add(drawable);
//        }
    }

    /**
     * Determines if a triangle should be culled based on back-face culling.
     * @param tri The triangle to be culled.
     * @return {@code true} if the triangle should be culled, {@code false} otherwise.
     * @implNote If {@link Settings#sceneProperties#backFaceCulled(GTri)} is false, this exits early.
     */
    public boolean backFaceCulled(Triangle tri) {
        if (tri.isDoubleSided()) return false;
        if (!Settings.sceneProperties.useBackFaceCulling.getValue()) return false;
        Vector3 N = Winding.normal(tri.getP1(), tri.getP2(), tri.getP3());
        Vector3 P = StaticRefs.getCamera().getPosition().sub(tri.getP1());
        return N.dot(P) < 0;
    }

    public abstract void sort();
}
