package com.j3d.engine.geometry.geo2d;

import com.j3d.J3DSettings;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.BasePoint;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.matrix.MatrixMath;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * CartesianPoint, not to be confused with {@link ScreenPoint}, represents
 * a point in 2D space.
 * (On the Cartesian Plane, where (0, 0) is the centre of the window.)
 * <p>
 * All 2d points should be calculated in {@link CartesianPoint}, but when you
 * want to show it on the screen, converted to a {@link ScreenPoint}
 * @author Lehlogonolo Poole
 * @see ScreenPoint
 * @see CartesianPoint#toScreen(Renderer)
 * @see BasePoint
 * @see Vector3
 */
public class CartesianPoint extends BasePoint<Double> {

    /**
     * used for comparing double values without running into
     * floating point errors.
     */
    private static final double EPSILON = 0.01;

    /**
     * Default Constructor.
     * Constructs a null CartesianPoint. This is probably a bad idea.
     */
    public CartesianPoint() {
        super(null, null);
    }

    /**
     * If this Cartesian Point is empty
     * @return boolean
     */
    public boolean isNotEmpty() {
        return x != null || y != null;
    }

    /**
     * Constructor with X and Y
     * @param X X-coordiante
     * @param Y Y-coordinate
     */
    public CartesianPoint(double X, double Y) {
        super(X, Y);
    }

    /**
     * Converts the Cartesian Point to a {@link ScreenPoint} such that it can be viewed on the user's window.
     * @param renderer The renderer instance.
     * @return A ScreenPoint
     */
    public ScreenPoint toScreen(Renderer renderer) {
        double adjustedX = x * Settings.sceneProperties.scale.getValue();
        double adjustedY = y * Settings.sceneProperties.scale.getValue();

        int screenX = (int) (adjustedX + (double) renderer.screenSize.width / 2);
        int screenY = (int) ((double) renderer.screenSize.height / 2 - adjustedY);


        return new ScreenPoint(screenX - J3DSettings.OFFSET_X, screenY);
    }

    /**
     * Converts the cartesian point to a {@link Vector3} and places it along the camera's
     * plane.
     * @param camera The camera instance.
     * @return A Vector3
     */
    public Vector3 toVector3(Camera camera) {
        Vector3 e = camera.getProjectionPlane();
        //TODO: When orbiting the camera left and right, the points tend to move outside of camera view.

        double dx = (x / Settings.sceneProperties.scale.getValue()) - e.getX();
        double dy = (y / Settings.sceneProperties.scale.getValue()) - e.getY();
        double dz = e.getZ();

        Vector3 camSpacePoint = new Vector3(dx, dy, dz);

        Vector3 rotated = Vector3.of(
                MatrixMath.mult(
                        camera.getRotation().camToWorld().matrix(),
                        camSpacePoint
                )
        );

        return rotated.add(camera.getPosition());
    }

    /**
     * Converts a 2 dimensional array into a cartesian point.
     * <p>
     * Generally used by Jaiva Implementation where points can be represented by [X, Y]
     * @param arr The array.
     * @return The new cartesian point
     */
    public static CartesianPoint fromList(ArrayList<? extends Number> arr) {
        if (arr == null || arr.size() != 2) {
            throw new IllegalArgumentException("Input list must contain exactly 2 numbers.");
        }
        // Use doubleValue() to preserve floating-point precision
        return new CartesianPoint(arr.get(0).doubleValue(), arr.get(1).doubleValue());
    }

    /**
     * Reverse of {@link CartesianPoint#fromList(ArrayList)}
     * @return A 2 Dimensional ArrayList of doubles.
     */
    public ArrayList<Double> toArray() {
        return new ArrayList<>(Arrays.asList(x, y));
    }

    @Override
    public String toString() {
        return ("{" + x + "; " + y + "}");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartesianPoint other)) return false;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        // Use the same epsilon rounding for consistent hashing
        return Objects.hash(Math.round(x / EPSILON), Math.round(y / EPSILON));
    }

    /**
     * Calculates the distance between this point and another point.
     * @param other The other point.
     * @return The distance.
     */
    public double distanceTo(CartesianPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the distance between this point and another point without the square root.
     * @param other The other point.
     * @return The distance.
     */
    public double distanceSquaredTo(CartesianPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return dx * dx + dy * dy;
    }
}
