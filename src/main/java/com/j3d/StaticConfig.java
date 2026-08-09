package com.j3d;

import com.j3d.engine.scene.draw.ViewType;
import com.j3d.engine.scene.draw.PureSortMethod;
import com.j3d.engine.math.Dim;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.gen.settings.Settings;
import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.users.User;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * While apart from settings, core settings are the modernied versio.
 * Settings who the user should not be able to access and thus is not available on the UI.
 */
public abstract class StaticConfig {
    /**
     * A constant number to offset all components which get pushed down by the JMenuBar
     */
    public static final int jMenuBarOffsetY = 20;
    /**
     * A constant offset used within {@link #toScreen()} and
     * {@link #toPoint()} to shift all x values {@code 200} pixels closer to the centre
     * of the screen.
     */
    public static final int OFFSET_X = 0;
    public static User user;
    public static boolean hasSaved = false;
    /**
     * Generic lock flag for when the user has CAPS LOCK enabled.
     */
    public static boolean lock;
    /**
     * The default screen size for the SceneManager.
     */
    public static Dim screenSize = new Dim(1800, 1000);
    /**
     * Flag to determine if back-face culling is used during rendering.
     */
    public static boolean useBackFaceCulling = true;
    /**
     * Flag to determine if triangle distances from the camera are displayed.
     */
    public static boolean showTriDistances = false;
    /**
     * Flag to determine if depth information is displayed.
     */
    public static boolean showDepth = false;
    /**
     * Flag to determine if normals are displayed.
     */
    public static boolean showNormals = false;
    /**
     * How the objects should be drawn.
     */
    public static ViewType viewType = ViewType.NORMAL;
    public static boolean movementControls = true;

    /// Literally could be set by [com.j3d.ui.home.Projects] or something else.
    public static void defaultLogin() {
        if (user == null) {
            user = DatabaseManager.tblUsers.findById(1);
        }
    }

    static {
        try {
            StaticConfig.lock =
                    Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        } catch (UnsupportedOperationException ue) {
            StaticConfig.lock = false;
        }
    }

    public static void setTriangleSortMethod(PureSortMethod method) {
        Settings.sceneProperties.triangleSortMethod.setValue(method);
        StaticRefs.getSceneManager().getRenderer().setSortMethod(method);
    }

    public static boolean isUseBackFaceCulling() {
        return useBackFaceCulling;
    }

    public static void setUseBackFaceCulling(boolean useBackFaceCulling) {
        StaticConfig.useBackFaceCulling = useBackFaceCulling;
    }

    public static boolean isShowTriDistances() {
        return showTriDistances;
    }

    public static void setShowTriDistances(boolean showTriDistances) {
        StaticConfig.showTriDistances = showTriDistances;
    }

    public static boolean isShowDepth() {
        return showDepth;
    }

    public static void setShowDepth(boolean showDepth) {
        StaticConfig.showDepth = showDepth;
    }

    public static boolean isShowNormals() {
        return showNormals;
    }

    public static void setShowNormals(boolean showNormals) {
        if (!showNormals) {
            StaticRefs.getSceneManager().layers.stream()
                    .flatMap(Layer::usableLayersStream)
                    .flatMap(Thing::objectsStream)
                    .filter(t -> t instanceof GTri)
                    .map(t -> (GTri) t)
                    .forEach(t -> t.showNorm = false);
        }
        StaticConfig.showNormals = showNormals;
    }

    public static ViewType getViewType() {
        return viewType;
    }

    public static void setViewType(ViewType viewType) {
        StaticConfig.viewType = viewType;
    }
}
