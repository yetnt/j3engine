package com.j3d.engine.react.events.payloads;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.rot.Rotation;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.scene.Camera;

public class CameraUpdatedEventPayload extends EventPayload<Camera> {

    private Vector3 position;
    private Rotation rotation;

    public CameraUpdatedEventPayload(Vector3 pos, Rotation rot) {
        super(StaticRefs.getCamera());
        this.position = pos;
        this.rotation = rot;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vector3 getPosition() {
        return position;
    }
}
