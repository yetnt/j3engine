package com.j3d.engine.scene.nodes;

import com.j3d.engine.scene.SceneObject;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.CleanableAction;
import com.j3d.engine.react.actions.DirtyVoidAction;
import com.j3d.engine.react.history.History;
import com.j3d.StaticConfig;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.ui.dialog.Spinner;
import com.j3d.ui.engine.floating.tree.LayerTree;
import com.j3d.ui.engine.floating.tree.TreeNodeIdentity;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.BiConsumer;

/**
 * A special kind of {@link SceneObject} which is almost all the time just a collection of other
 * {@code SceneObject}s but with the extra role of having to:
 * <ul>
 *     <li>Be apart of the {@link LayerTree}</li>
 *     <li>Be hidden and deleted via the {@link History} using {@link Action}s</li>
 *     <li>Be serialized by {@link ProjectFile} and reconstructed off the EDT thread via {@link #invokeSwingHooks()}</li>
 * </ul>
 * @see LayerTree
 * @see History
 * @see Action
 * @see ProjectFile
 * @see Thing
 * @see Layer
 * @see #invokeSwingHooks()
 * @author Lehlogonolo Poole
 */
public interface SceneObjectList extends SceneObject {
    /**
     * Whether the given object is hidden or not
     * @return true if hidden, false otherwise
     */
    boolean isHidden();

    /**
     * Hides the object. This is for internal use. GUI and other user
     * things should make use of {@link #toggleVisibility()} instead.
     */
    void setHidden(boolean hidden);

    /**
     * Labels the object as for deletion. Has similar implications as
     * being hidden however it will be cleaned up later and won't be
     * available on lists.
     * @return true if the object was marked for deletion
     */
    boolean isForDeletion();

    /**
     * Marks the object for deletion.
     */
    void setForDeletion(boolean forDeletion);

    /**
     * Toggles the visibility of the object.
     * @return An action that toggles the visibility of the object and returns
     * the new visibility state.
     */
    Action<Boolean> toggleVisibility();

    /**
     * Deletes the object.
     * <p>
     *     The use of the word "later" is because the action of deletion
     *     is only ever done once the Action object is out of the history's
     *     bounds. At that point {@link CleanableAction#cleanup()} is called,
     *     which actually deletes.
     * </p>
     * @return An action that deletes the object.
     */
    DirtyVoidAction deleteLater();

    default void toggleSaved() {
        StaticConfig.hasSaved = false;
    };

    /**
     * An internal delete method which all implementors should
     * override for instantaneous deletion.
     * <p>
     *     This method should not be called by a user interface things.
     *     However, any object overriding the method must provide a way
     *     to delete everything relating to it.
     * </p>
     */
    void instantDelete();

    /**
     * Returns the {@link TreeNodeIdentity} for the given object to use within the {@link com.j3d.ui.engine.floating.tree.LayerTree}
     * @return The identity
     */
    TreeNodeIdentity<? extends SceneObjectList> getIdentity();

    /**
     * The actual tree node for the representation of this object within {@link com.j3d.ui.engine.floating.tree.LayerTree}
     * @return The tree node
     */
    DefaultMutableTreeNode getTreeNode();

    /**
     * The callback for when the object is selected in the tree.
     * @return The callback
     */
    BiConsumer<? extends SceneObjectList, DefaultMutableTreeNode> getOnSelect();

    /**
     * A method to initialise anything swing related within the constructor.
     * This is typically called within the SceneObjectList's constructor who needs to
     * create an object outside the EDT thread.
     * <p>
     *     A use case (the current use case) would be when an SceneObjectList is loaded
     *     from disk to memory and the {@link Spinner} dialog is
     *     blocking the EDT thread. After the throbber has finished, you may call invokeSwingHooks safely
     *     rather than fighting with a blocked EDT thread.
     * </p>
     */
    void invokeSwingHooks();
}
