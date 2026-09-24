package com.j3d.engine.math;

import com.j3d.engine.math.convert.Conversion;
import com.j3d.engine.math.convert.ConversionWithOffset;
import com.j3d.engine.math.matrix.Vector3;

import java.awt.*;

/**
 * ScreenPoint, not to be confused with {@link CartesianPoint}, is a point on the actual screen (window) open.
 * Where (0, 0) is the top left corner of the window.
 * <p>
 *     All 2d points should be calculated in {@link CartesianPoint}, but when you want to show it on the screen, converted to a {@link ScreenPoint}
 * </p>
 * @author Lehlogonolo Poole
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
     * Converts this ScreenPoint back into it's CartesianPoint using the global Conversion properties
     * defined by {@link Conversion#global()}
     * @return A CartesianPoint
     */
    public CartesianPoint toPoint() {
        return
                toPoint(Conversion.global());
    }

    /**
     * Converts the Screen Point into a {@link CartesianPoint}.
     * @param conversion The conversion properties
     * @return The {@link CartesianPoint} coordinates of this ScreenPoint.
     * @implSpec If this is being used to reverse {@link CartesianPoint#toScreen(Conversion)}, it will not be accurate.
     * @implNote This is just a wrapper over {{@link #toPoint(ConversionWithOffset)}}
     */
    public CartesianPoint toPoint(Conversion conversion) {
        return toPoint(
                ConversionWithOffset.from(conversion)
        );
    }
    /**
     * Converts the Screen Point into a {@link CartesianPoint}.
     * @param conversion The conversion properties
     * @return The {@link CartesianPoint} coordinates of this ScreenPoint.
     * @implSpec If this is being used to reverse {@link CartesianPoint#toScreen(ConversionWithOffset)}, it will not be accurate.
     */
    public CartesianPoint toPoint(ConversionWithOffset conversion) {
        double adjustedX = (((x)- conversion.size().width / 2.0) / conversion.scale()) - conversion.offset().getXOffset();
        double adjustedY = ((conversion.size().height / 2.0 - y) / conversion.scale()) -  conversion.offset().getYOffset();
        return new CartesianPoint(adjustedX, adjustedY);
    }

    /**
     * Returns the ScreenPoint as {@link Point}
     * @return A Point
     */
    public Point toSwingPoint() {
        return new Point(x, y);
    }

    public String toMacroPoint() {
        return "[" + x + ";" + y + "]";
    }

    public static ScreenPoint fromMacroPoint(String macroPoint) {
        String arg = macroPoint.substring(1, macroPoint.length() - 1);
        String[] split = arg.split(";");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new ScreenPoint(x, y);
    }

    @Override
    public String toString() {
        return "SP[" +
                "x=" + x +
                ", y=" + y +
                ']';
    }
}
