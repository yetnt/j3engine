package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.utility.Pair;

import java.awt.event.MouseEvent;

public class ScaleMouseOwner extends TransformMouseOwner {

    Handle handle;

    public ScaleMouseOwner() {
        super(MOwner.SCALE_HANDLE);
    }

    @Override
    public void mouseDraggedAdapter(HandleType selectedHandle, int dx, int dy, MouseEvent e) throws Exception {
        handle = handles.stream().filter(
                h -> h.handleType == selectedHandle
        ).findFirst().orElseThrow();

        CartesianPoint Cnormal = new ScreenPoint(e.getX(), e.getY()).toPoint(Static.renderer);
        Vector3 C = Cnormal.toVector3(Static.camera);
        Vector3 A = handle.position;
        Vector3 camPos = Static.camera.getPosition();

        Pair<Vector3, Double> newPos = calculateNewHandlePos(
                camPos,
                C.sub(camPos).normalize(),
                A,
                switch (handle.handleType) {
                    case X -> new Vector3(1, 0, 0);
                    case Y -> new Vector3(0, 1, 0);
                    case Z -> new Vector3(0, 0, 1);
                }
        );

        Vector3 D = newPos.first;

        // TODO: Z handle is stuck??

        handle.setPreview(D);

        Static.mainPanel.repaint();

        // TODO: draw a copy of handle at position D and apply whatever we're doing. Maybe for later
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
        handle.disablePreview();
    }
}
