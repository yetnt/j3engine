package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.Static;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.input.mouse.MOwner;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RotateMouseOwner extends TransformMouseOwner {

    public Handle handle;

    public RotateMouseOwner() {
        super(MOwner.ROTATE_HANDLE);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
    }

    @Override
    public void setHandles(ArrayList<Handle> handles, ArrayList<GPoint> references) {
        super.setHandles(handles, references);
        int axisLength = 10;
        Vector3 center = Vector3.reduceToVector3(
                references.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                , Vector3::add).div(references.size());
        handles.forEach(
                h -> {
                    h.extraDetail(g -> {
                        Vector3 axis = new Vector3(), A = new Vector3();
                        switch (h.handleType()) {
                            case X -> {
                                axis = new Vector3(0, 1, 1);
                                A = h.getPos().add(new Vector3(axisLength, 0, 0));
                                g.setColor(Color.RED);
                            }
                            case Y -> {
                                axis = new Vector3(1, 0, 1);
                                A = h.getPos().add(new Vector3(0, axisLength, 0));
                                g.setColor(Color.BLUE);
                            }
                            case Z -> {
                                axis = new Vector3(1, 1, 0);
                                A = h.getPos().add(new Vector3(0, 0, axisLength));
                                g.setColor(Color.GREEN);
                            }
                        }
                        Vector3 B = center.add((A.sub(center)).rotateAroundAxis(axis, 180));
                        Static.renderer.drawLine3D(g, A, B, Static.camera);
                        g.setColor(Color.WHITE);
                    }
                    );
                }
        );
    }
}
