package com.j3d.engine.draw;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo2d.DecomposeWhenDrawn;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
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
 * @see PureListener
 * @see SceneRenderer
 */
public abstract class SortMethod extends ArrayList<RenderState<?, ?>> {
    /**
     * A static list of registered PureListener objects.
     */
    protected final ArrayList<PureListener> registered;

    /**
     * Constructor for SortMethod
     *
     * @param registered The list of PureListener objects to be registered.
     */
    public SortMethod(ArrayList<PureListener> registered) {
        super();
        this.registered = registered;
    }

    @Override
    public boolean add(RenderState<?, ?> drawable) {
        SortMethod sm = this;
//        if (drawable instanceof Pure s) {
//            if (s.isValid())
//                return super.add(drawable);
//            else {
//                SceneRenderer.unregister(s);
//                this.remove(s);
//                return false;
//            }
//        }
        if (drawable instanceof DecomposeWhenDrawn<?> d) {
            d.getDecomposeList().forEach(r-> {
                if (r.isValid())
                    sm.add(drawable);
                else {
                    StaticRefs.getSceneManager().getRenderer().unregister(drawable);
                    sm.remove(d);
                }
            });
            return true;
        } else {
            return super.add(drawable);
        }
    }

    /**
     * Determines if a triangle should be culled based on back-face culling.
     * @param tri The triangle to be culled.
     * @return {@code true} if the triangle should be culled, {@code false} otherwise.
     * @implNote If {@link Settings#sceneProperties#backFaceCulled(GTri)} is false, this exits early.
     * Also quite buggy TODO Fix this or enforce consistent winding of triangles.
     */
    public boolean backFaceCulled(GTri tri) {
        if (tri.isDoubleSided()) return false;
        if (!Settings.sceneProperties.useBackFaceCulling.getValue()) return false;
        Vector3 N = tri.normal();
        Vector3 P = StaticRefs.getCamera().getPosition().sub(tri.getLegA().getA().getPivot());
        return N.dot(P) < 0;
    }
}
