package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.Static;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.react.events.EventPayload;

public class ChangeCentreEventPayload extends EventPayload<TransformMouseOwner> {
    private ScreenPoint mousePos;
    /**
     * Default Constructor for EventPayload
     *
     * @param e The initiator of the broadcast.
     * @param position Mouse position
     */
    public ChangeCentreEventPayload(TransformMouseOwner e, ScreenPoint position) {
        super(e, Static.sceneManager);
        this.mousePos = position;
    }

    public ScreenPoint getMousePos() {
        return mousePos;
    }
}
