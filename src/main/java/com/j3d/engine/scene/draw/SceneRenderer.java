package com.j3d.engine.scene.draw;

import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.scene.draw.methods.SortMethod;
import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.methods.CamDepthSort;
import com.j3d.engine.scene.draw.methods.CamDistSort;
import com.j3d.engine.scene.draw.methods.DDUUIDSort;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.ui.engine.J3DPanel;

import java.awt.*;
import java.util.ArrayList;

/**
 * SceneRenderer is a class which has the sole responsibility of referencing a single {@link SortMethod}
 * and hence drawing every single {@link com.j3d.engine.geometry.Pure} wrapped in a {@link RenderState}
 * which is provided to it.
 * <p>
 *     The "methods" which in reality are just plain {@link ArrayList} with special sorting done when things are
 *     added. Examples include:
 *     <ul>
 *         <li>{@link CamDistSort} sorts by Euclidean distance to the camera</li>
 *         <li>{@link CamDepthSort} sorts by camera's depth value</li>
 *         <li>{@link DDUUIDSort} attempts to sort by <strong>D</strong>istance, then <strong>D</strong>epth, and finally falls
 *         back to <strong>UUID</strong></li>
 *     </ul>
 * </p>
 * @author Lehlogonolo Poole
 * @see GTri
 * @see PureSortMethod
 * @see SortMethod
 * @see CamDepthSort
 * @see CamDistSort
 * @see DDUUIDSort
 */
public final class SceneRenderer {
    /**
     * A list used for sorting {@link RenderState} objects based on whichever method SceneRenderer uses.
     * The list is cleared after each render cycle.
     * @implNote This more references a deque but for reusability and clarity
     * an ArrayList was used instead.
     * This queue is not also a pure ArrayList at runtime
     * but rather a subclass of {@link SortMethod}. Which is how the triangle
     * sorting is done.
     */
    private SortMethod queue;

    public SceneRenderer() {
        // later set bucket sort to
        setSortMethod(PureSortMethod.CAMDISTSORT);
    }
    /**
     * Sets the sort method for SceneRenderer.
     * @param method The PureSortMethod to set.
     */
    public void setSortMethod(PureSortMethod method) {
        queue = switch (method) {
            case CAMDISTSORT, NONE -> new CamDistSort();
            case CAMDEPTHSORT -> new CamDepthSort();
            case DDUUIDSORT -> new DDUUIDSort();
            //case BUCKETSORT ->new BucketSort(registered);
            default -> new CamDistSort();
        };
    }

    /**
     * Registers a {@link RenderState} with SceneRenderer.
     * @implSpec This method should only be called by {@link com.j3d.engine.scene.nodes.Thing}
     * @param renderState The render state to register.
     */
    public void register(RenderState<?, ?> renderState) {
        queue.add(renderState);
    }

    /**
     * Clears the SceneRenderer queue.
     */
    public void clearQueue() {
        queue.clear();
    }

    /**
     * Draws all the Render State that have been sorted within the queue.
     * @param g The Graphics2D context.
     * @implNote This method will clear the queue after drawing, as its expected the flow to be as follows:
     * <ol>
     *     <li>{@link J3DPanel} calls {@link SceneManager#draw(Graphics2D)}</li>
     *     <li>{@link SceneManager} calls {@link Layer#draw(Graphics2D)}</li>
     *     <li>{@link Layer} calls {@link Thing#draw(Graphics2D)}</li>
     *     <li>A {@link Thing} calls {@link #register(RenderState)}
     *     for all its constituent objects.</li>
     *     <li>Finally, back in {@link SceneManager#draw(Graphics2D)}, it calls, this method.</li>
     *     <li>Repeat each draw/frame (Don't ask me how the engine runs so well at almost 10k triangles even when we do this every single redraw/frame)</li>
     * </ol>
     * @see J3DPanel#paint(Graphics) 
     * @see SceneManager#draw(Graphics2D) 
     * @see Layer#draw(Graphics2D) 
     * @see Thing#draw(Graphics2D)
     */
    public void draw(Graphics2D g) {
        queue.sort();
        for  (RenderState<?, ?> drawable : new ArrayList<>(queue)) {
            if (!drawable.isValid()) continue;
            if (StaticRefs.throwIfNull(StaticRefs.getSceneManager()).getSelected().contains(drawable.getParent()))
                drawable.drawSelected(g);
            else drawable.draw(g);
        }
        clearQueue();
    }

}
