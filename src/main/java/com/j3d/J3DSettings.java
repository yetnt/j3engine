package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.draw.ViewType;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.draw.tris.TriangleSortMethod;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.settings.Settings;
import com.j3d.utility.Pair;

public class J3DSettings {

    /**
     * A constant number to offset all components which get pushed down by the JMenuBar
     */
    public static final int jMenuBarOffsetY = 20;
    /**
     * A constant offset used within {@link CartesianPoint#toScreen(Renderer)} and
     * {@link ScreenPoint#toPoint(Renderer)} to shift all x values {@code 200} pixels closer to the centre
     * of the screen.
     */
    public static final int OFFSET_X = 200;
    /**
     * The default screen size for the Renderer.
     */
    public static Dimension screenSize = new Dimension(1800, 1000);
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
    /**
     * The current project open in the engine.
     * First element is the project path
     * Second element is the project file name
     */
    private static Pair<String, String> project;

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

    public static Pair<String, String> getProject() {
        return project;
    }

    public static void setProject(String path, String name) {
        Settings.projectFile.setValue(path);
        project = new Pair<>(path, name);
    }
}
