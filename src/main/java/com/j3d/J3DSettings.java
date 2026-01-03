package com.j3d;

import com.j3d.engine.draw.TriStateArea;
import com.j3d.engine.draw.TriangleSortMethod;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;

public class J3DSettings {
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
}
