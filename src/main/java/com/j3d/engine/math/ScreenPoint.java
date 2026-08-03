package com.j3d.engine.math;

import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.StaticConfig;
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
        double adjustedX = ((x + StaticConfig.OFFSET_X) - sceneManager.screenSize.width / 2.0) / Settings.sceneProperties.scale.getValue();
        double adjustedY = (sceneManager.screenSize.height / 2.0 - y) / Settings.sceneProperties.scale.getValue();

        return new CartesianPoint(adjustedX, adjustedY);
    }

    public CartesianPoint toPointWithProps(double scale, Dim size) {
        double adjustedX = ((x)- size.width / 2.0) / scale;
        double adjustedY = (size.height / 2.0 - y) / scale;
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
