package com.j3d.engine.geometry.geo3d;

/**
 * Represents the virtual camera in the 3D scene.
 * It holds the position, orientation, and projection plane details
 * required to project 3D points onto a 2D surface.
 */
public class Camera {

    /**
     * The 3D position of the camera in the world (c).
     */
    private Vector3 position;

    /**
     * The orientation of the camera (Tait-Bryan angles: pitch, yaw, roll) (θ).
     */
    private Rotation rotation;

    /**
     * The position of the display surface relative to the camera (e).
     * The z-component of this vector acts as the focal length (zoom).
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
     * Default constructor that creates a camera at the origin, looking forward,
     * with a default projection plane distance.
     */
    public Camera() {
        this.position = new Vector3(0, 0, 0);
        this.rotation = new Rotation(0, 0, 0);
        // A default focal length (e.g., 500). e_x and e_y are 0 for a standard projection.
        this.projectionPlane = new Vector3(0, 0, 2);
    }

    public void move(Vector3 delta) {
        this.position = this.position.add(delta);
    }

    public Vector3 getPosition() {
        return position;
    }

    public Camera setPosition(Vector3 position) {
        this.position = position;
        return this;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Camera setRotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public Vector3 getProjectionPlane() {
        return projectionPlane;
    }

    public Camera setProjectionPlane(Vector3 projectionPlane) {
        this.projectionPlane = projectionPlane;
        return this;
    }

//    /**
//     * Computes the forward direction vector of the camera based on its rotation.
//     * This vector indicates the direction the camera is facing in 3D space.
//     *
//     * @return A normalized Vector3 representing the camera's forward direction.
//     */
    public Vector3 getForward() {
        double yaw = Math.toRadians(rotation.getYaw());     // horizontal angle
        double pitch = Math.toRadians(rotation.getPitch()); // vertical angle

//        return new Vector3(
//                Math.cos(pitch) * Math.sin(yaw),
//                Math.sin(pitch),
//                Math.cos(pitch) * Math.cos(yaw)
        return new Vector3(
                        Math.cos(yaw) * Math.sin(pitch),
                        Math.sin(yaw),
                        Math.cos(yaw) * Math.cos(pitch)
                ).normalize();
    }

    /**
     * Adjusts the camera's rotation to look at a specific target point in 3D space.
     *
     * @param target The target point to look at.
     */
    public void lookAt(Vector3 target) {
        Vector3 direction = target.sub(this.position).normalize();
        double yaw = Math.toDegrees(Math.asin((direction.getY())));
        double pitch = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
        this.rotation = new Rotation(pitch, yaw, 0);
    }


    @Override
    public String toString() {
        return "Camera{" + "position=" + position + ", rotation=" + rotation + ", projectionPlane=" + projectionPlane + '}';
    }
}