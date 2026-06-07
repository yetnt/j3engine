package com.j3d.engine.geometry;

import com.j3d.J3DSettings;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.gen.settings.Settings;

import java.awt.*;

/**
 * ScreenPoint, not to be confused with {@link CartesianPoint}, is a point on the actual screen (window) open.
 * Where (0, 0) is the top left corner of the window.
 * <p>
 *     All 2d points should be calculated in {@link CartesianPoint}, but when you want to show it on the screen, converted to a {@link ScreenPoint}
 * </p>
 * @author Lehlogonolo Poole
 * @see CartesianPoint#toScreen(SceneManager)
 * @see Vector3
 * @see BasePoint
 */
public class ScreenPoint extends BasePoint<Integer> {

    /**
     * Default Constructor
     * @param X The X-Value
     * @param Y The Y-Value
     */
    public ScreenPoint(int X, int Y) {
        super(X, Y);
    }

    /**
     * Converts this ScreenPoint back into it's CartesianPoint. (Accuracy is not guaranteed.)
     * @param sceneManager The SceneManager Instance.
     * @return A CartesianPoint
     */
    public CartesianPoint toPoint(SceneManager sceneManager) {
        double adjustedX = ((x + J3DSettings.OFFSET_X) - sceneManager.screenSize.width / 2.0) / Settings.sceneProperties.scale.getValue();
        double adjustedY = (sceneManager.screenSize.height / 2.0 - y) / Settings.sceneProperties.scale.getValue();

        return new CartesianPoint(adjustedX, adjustedY);
    }

    /**
     * Returns the ScreenPoint as {@link Point}
     * @return A Point
     */
    public Point toSwingPoint() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "SP[" +
                "x=" + x +
                ", y=" + y +
                ']';
    }
}
