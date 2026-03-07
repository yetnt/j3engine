package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.geo3d.matrix.Matrix4;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

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

    public Vector3 getForward() {
        return rotation.matrix().transform(new Vector3(0, 0, 1)).normalize();
    }

    // Right vector (local X-axis, fully respects roll)
    public Vector3 getRight() {
        double yawRad = Math.toRadians(this.rotation.getYaw());
        double pitchRad = Math.toRadians(this.rotation.getPitch());
        double rollRad = Math.toRadians(this.rotation.getRoll());

        // Apply YPR rotation to local X-axis (1,0,0)
        double x = Math.cos(yawRad) * Math.cos(rollRad) + Math.sin(yawRad) * Math.sin(pitchRad) * Math.sin(rollRad);
        double y = Math.cos(pitchRad) * Math.sin(rollRad);
        double z = -Math.sin(yawRad) * Math.cos(rollRad) + Math.cos(yawRad) * Math.sin(pitchRad) * Math.sin(rollRad);

        return new Vector3(x, y, z).normalize();
    }

    // Up vector (local Y-axis, orthogonal to forward & right)
    public Vector3 getUp() {
        Vector3 forward = getForward();
        Vector3 right = getRight();
        return right.cross(forward).normalize(); // ensures orthogonal up
    }

//    public void lookAt(Vector3 target) {
//        Vector3 dir = target.sub(this.position).normalize();
//
//        // Yaw = rotation around vertical (y-axis), left/right
//        double yawDeg = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
//
//        // Pitch = rotation up/down
//        double pitchDeg = Math.toDegrees(Math.atan2(-dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ())));
//
//        // Roll = nod, optional, usually 0 if you don't want camera tilt
//        double rollDeg = 0;
//
//        this.rotation = new Rotation(yawDeg, pitchDeg, rollDeg);
//    }

    public void lookAt(Vector3 target) {
        Vector3 forward = target.sub(this.position).normalize();
        Vector3 worldUp = new Vector3(0, 1, 0);

        // Handle case where forward is parallel to worldUp
        if (Math.abs(forward.dot(worldUp)) > 0.999) {
            worldUp = new Vector3(0, 0, 1);
        }

        Vector3 right = worldUp.cross(forward).normalize();
        Vector3 up = forward.cross(right).normalize();

        // Build rotation matrix from basis vectors
        Matrix4 rotMatrix = new Matrix4(new double[][]{
                {right.getX(),   right.getY(),   right.getZ(),   0},
                {up.getX(),      up.getY(),      up.getZ(),      0},
                {forward.getX(), forward.getY(), forward.getZ(), 0},
                {0,              0,              0,              1}
        });

        // Extract Tait-Bryan angles from matrix to store in Rotation
        double pitch = Math.toDegrees(Math.asin(-rotMatrix.m[1][2]));
        double yaw   = Math.toDegrees(Math.atan2(rotMatrix.m[0][2], rotMatrix.m[2][2]));
        double roll  = 0;

        this.rotation = new Rotation(yaw, pitch, roll);
    }


    @Override
    public String toString() {
        return "Camera{" + "position=" + position + ", rotation=" + rotation + ", projectionPlane=" + projectionPlane + '}';
    }
}