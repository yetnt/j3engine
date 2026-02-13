package com.j3d;

import com.j3d.engine.draw.ViewType;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.draw.tris.TriangleSortMethod;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.utility.Pair;

public class J3DSettings {

    /**
     * A constant number to offset all components which get pushed down by the JMenuBar
     */
    public static final int jMenuBarOffsetY = 20;
    /**
     * The default screen size for the Renderer.
     */
    public static Dimension screenSize = new Dimension(1800, 1000);
    /**
     * Factor to scale the {@link CartesianPoint} vs {@link ScreenPoint} units.
     * <p>
     * This is such that the screen space is not used as the default grid. Where (0, 1) and (0, 0) are but a pixel apart.
     * The Scale factor helps by making it such that (if SCALE is set to 10), inputting (0, 1) as a {@link CartesianPoint}, when converted to {@link ScreenPoint} it is multiplied by 10 units.
     */
    public static double SCALE = 10.0;
    /**
     * The method used for sorting triangles in the rendering process.
     */
    private static TriangleSortMethod triangleSortMethod = TriangleSortMethod.NONE;
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
    public static double cameraMoveSpeed = 0.3;
    /**
     * The current project open in the engine.
     * First element is the project path
     * Second element is the project file name
     */
    private static Pair<String, String> project;

    public static TriangleSortMethod getTriangleSortMethod() {
        return triangleSortMethod;
    }
    public static void setTriangleSortMethod(TriangleSortMethod method) {
        triangleSortMethod = method;
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
        project = new Pair<>(path, name);
    }
}
