package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.geo3d.matrix.MatrixMath;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.geometry.geo3d.rot.Rotation;

/**
 * Represents the virtual camera in the 3D scene.
 * <p>
 * The camera defines the viewpoint from which the scene is rendered. It holds its
 * own position and orientation (rotation) in world space. It also contains the
 * projection plane, which determines the field of view and focal length for
 * the 3D-to-2D projection.
 * @author Lehlogonolo Poole
 * @see Rotation
 * @see Vector3
 * @see MatrixMath
 */
public class Camera {

    /**
     * The 3D position of the camera in the world (c).
     */
    private Vector3 position;

    /**
     * The orientation of the camera, defined by pitch, yaw, and roll angles.
     */
    private Rotation rotation;

    /**
     * The position of the display surface relative to the camera's local coordinate system.
     * The Z-component of this vector is the most critical, acting as the focal length (zoom).
     * A larger Z-value results in a narrower field of view (more zoom).
     */
    private Vector3 projectionPlane;

    /**
     * Constructs a new Camera with a specified position, rotation, and projection plane.
     *
     * @param position        The camera's position in 3D space.
     * @param rotation        The camera's orientation.
     * @param projectionPlane The camera's projection plane settings.
     */
    public Camera(Vector3 position, Rotation rotation, Vector3 projectionPlane) {
        this.position = position;
        this.rotation = rotation;
        this.projectionPlane = projectionPlane;
    }

    /**
     * Default constructor that creates a camera at the origin (0,0,0), with zero rotation,
     * looking along the world's Z-axis. It uses a default projection plane.
     */
    public Camera() {
        this.position = Vector3.ZERO;
        this.rotation = new Rotation(0, 0, 0);
        // A default focal length (e.g., 500). e_x and e_y are 0 for a standard projection.
        this.projectionPlane = Vector3.Z(2);
    }

    /**
     * Moves the camera by a given delta vector.
     *
     * @param delta The vector to add to the camera's current position.
     */
    public void move(Vector3 delta) {
        this.position = this.position.add(delta);
    }

    /**
     * Gets the position
     * @return The camera's position in 3D space.
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Sets the position
     * @param position The new position for the camera.
     */
    public Camera setPosition(Vector3 position) {
        this.position = position;
        return this;
    }

    /**
     * Gets the rotation
     * @return The camera's orientation.
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation
     * @param rotation The new rotation for the camera.
     */
    public Camera setRotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * Gets the projection plane
     * @return The camera's projection plane settings.
     */
    public Vector3 getProjectionPlane() {
        return projectionPlane;
    }

    /**
     * Sets the projection plane
     * @param projectionPlane The new projection plane settings.
     */
    public Camera setProjectionPlane(Vector3 projectionPlane) {
        this.projectionPlane = projectionPlane;
        return this;
    }

    /**
     * Calculates the camera's forward-facing direction vector in world space.
     * This is determined by transforming a local "forward" vector (0, 0, 1)
     * using the camera's current yaw and pitch. Roll is ignored to maintain a stable horizon.
     *
     * @return A normalized {@link Vector3} representing the direction the camera is pointing.
     */
    public Vector3 getForward() {
        return Vector3.of(
                MatrixMath.mult(
                        MatrixMath.mult(
                                rotation.camToWorld().rotYaw(),
                                rotation.camToWorld().rotPitch()
                        ),
                        // The local forward vector
                        Vector3.Z
                )
        ).normalize();
    }

    /**
     * Calculates the camera's right-facing direction vector in world space.
     * This is determined by transforming a local "right" vector (1, 0, 0)
     * using the camera's current yaw and pitch. Roll is ignored.
     * @return A normalized {@link Vector3} representing the direction the camera is pointing.
     */
    public Vector3 getRight() {
        return Vector3.of(
                MatrixMath.mult(
                        MatrixMath.mult(
                                rotation.camToWorld().rotYaw(),
                                rotation.camToWorld().rotPitch()
                        ),
                        // The local right vector
                        Vector3.X
                )
        ).normalize();
    }

    /**
     * Calculates the camera's up-facing direction vector in world space.
     * This is determined by transforming a local "up" vector (0, 1, 0)
     * using the camera's current yaw and pitch. Roll is ignored.
     * @return A normalized {@link Vector3} representing the direction the camera is pointing.
     */
    public Vector3 getUp() {
        return Vector3.of(
                MatrixMath.mult(
                        MatrixMath.mult(
                                rotation.camToWorld().rotYaw(),
                                rotation.camToWorld().rotPitch()
                        ),
                        // The local up vector
                        Vector3.Y
                )
        ).normalize();
    }

    /**
     * Orients the camera to face a specific target point in world space.
     * This method calculates the necessary pitch and yaw to align the camera's forward vector
     * with the direction from its current position to the target. The camera's roll is set to 0.
     *
     * @param target The world-space {@link Vector3} point to look at.
     */
    public void lookAt(Vector3 target) {
        Vector3 dir = target.sub(this.position).normalize();

        // Calculate yaw (horizontal angle) from the X and Z components
        double yaw = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
        // Calculate pitch (vertical angle) from the Y component and the horizontal distance
        double pitch = Math.toDegrees(Math.atan2(
                -dir.getY(),
                Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ())
        ));

        // Set the new rotation, keeping roll at 0 for a stable horizon and cuz u dont need to set it.
        this.rotation = new Rotation(pitch, yaw, 0);
    }

    @Override
    public String toString() {
        return "Camera{" + "position=" + position + ", rotation=" + rotation + ", projectionPlane=" + projectionPlane + '}';
    }

    public void setFocalLength(double d) {
        this.projectionPlane = new Vector3(
                this.projectionPlane.getX(),
                this.projectionPlane.getY(),
                d
        );
    }
}
