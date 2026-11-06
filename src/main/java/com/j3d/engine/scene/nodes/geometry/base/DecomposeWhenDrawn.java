package com.j3d.engine.scene.nodes.geometry.base;

import com.j3d.engine.geometry.Pure;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.nodes.geometry.*;

import java.util.ArrayList;

/**
 * Marker Interface which denotes that an instance of this class, (Usually a {@link GObject}) can
 * ve "decomposed" or otherwise broken down to simpler pure ({@link Pure}) geometry which is further
 * wrapped in a {@link RenderState} which then finally can be drawn by the {@link SceneRenderer}.
 * @implNote How one decomposes depends on the geometry. e.g.
 * <ul>
 *     <li>{@link GPoint} -> {@code 1 } {@link com.j3d.engine.geometry.Point}</li>
 *     <li>{@link GLine} -> {@code 1 } {@link com.j3d.engine.geometry.Segment}</li>
 *     <li>{@link GTri} -> {@code 1 } {@link com.j3d.engine.geometry.Triangle}</li>
 *     <li>{@link GCurve} -> {@code (N) } {@link com.j3d.engine.geometry.Segment}s</li>
 *     <li>{@link GPolygon} -> {@code (N) } {@link com.j3d.engine.geometry.Triangle}s</li>
 * </ul>
 * @param <T> The specific Pure class this can be decomposed to.
 * @author Lehlogonolo Poole
 */
public interface DecomposeWhenDrawn<T extends Pure> {
    /**
     * The actual decomposition.
     * @implSpec Implementors need to have a field to store the render state, or otherwise
     * implementing this is allowed to be a no-op
     */
    void decompose();

    /**
     * Fetches the actual decomposition list.
     * @return The arraylist of decomposed stuff
     */
    ArrayList<RenderState<T, GObject>> getDecomposeList();

    /**
     * Invalidates every single render state within {@link #getDecomposeList()}
     */
    default void invalidateAll() {
        getDecomposeList().forEach(RenderState::invalidate);
    }
}
