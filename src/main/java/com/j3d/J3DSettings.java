package com.j3d;

import com.j3d.engine.SceneManager;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.draw.tris.TriangleSortMethod;
import com.j3d.engine.geometry.Dim;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.gen.settings.Settings;

public class J3DSettings {

    /**
     * A constant number to offset all components which get pushed down by the JMenuBar
     */
    public static final int jMenuBarOffsetY = 20;
    /**
     * A constant offset used within {@link CartesianPoint#toScreen(SceneManager)} and
     * {@link ScreenPoint#toPoint(SceneManager)} to shift all x values {@code 200} pixels closer to the centre
     * of the screen.
     */
    public static final int OFFSET_X = 200;
    /**
     * The default screen size for the SceneManager.
     */
    public static Dim screenSize = new Dim(1800, 1000);
    /**
     * Flag to determine if back-face culling is used during rendering.
     */
    private static boolean useBackFaceCulling = false;
    /**
     * Flag to determine if triangle distances from the camera are displayed.
     */
    private static boolean showTriDistances = false;
    /**
     * Flag to determine if depth information is displayed.
     */
    private static boolean showDepth = false;
    /**
     * Flag to determine if normals are displayed.
     */
    private static boolean showNormals = false;
    /**
     * How the objects should be drawn.
     */
    private static ViewType viewType = ViewType.NORMAL;

    public static void setTriangleSortMethod(TriangleSortMethod method) {
        Settings.sceneProperties.triangleSortMethod.setValue(method);
        TriStateArea.setSortMethod(method);
    }

    public static boolean isUseBackFaceCulling() {
        return useBackFaceCulling;
    }
    public static void setUseBackFaceCulling(boolean useBackFaceCulling) {
        J3DSettings.useBackFaceCulling = useBackFaceCulling;
    }

    public static boolean isShowTriDistances() {
        return showTriDistances;
    }
    public static void setShowTriDistances(boolean showTriDistances) {
        J3DSettings.showTriDistances = showTriDistances;
    }

    public static boolean isShowDepth() {
        return showDepth;
    }
    public static void setShowDepth(boolean showDepth) {
        J3DSettings.showDepth = showDepth;
    }

    public static boolean isShowNormals() {
        return showNormals;
    }
    public static void setShowNormals(boolean showNormals) {
        J3DSettings.showNormals = showNormals;
    }

    public static ViewType getViewType() {
        return viewType;
    }
    public static void setViewType(ViewType viewType) {
        J3DSettings.viewType = viewType;
    }

}
