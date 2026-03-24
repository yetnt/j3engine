package com.j3d.engine.interact.cmd.commands.pan;

import com.j3d.Static;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.settings.Settings;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;

import java.awt.event.MouseEvent;

public class OrbitMouseOwner extends MouseOwner {
    private int startX, startY;
    public OrbitMouseOwner() {
        super(MOwner.ORBIT);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        startX = e.getX();
        startY = e.getY();
        CursorManager.set(CursorNames.HAND_GRAB);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        startX = 0;
        startY = 0;
        CursorManager.set(CursorNames.HAND_GRAB);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isNotOwner()) return;
        CursorManager.set(CursorNames.HAND_GRABBING);

        int dx = e.getX() - startX;
        int dy = e.getY() - startY;

        double dxScaled = scaleDifference(dx);
        double dyScaled = scaleDifference(dy);

        Static.camera.getRotation().setPitch(Static.camera.getRotation().getPitch() - dyScaled);
        Static.camera.getRotation().setYaw(Static.camera.getRotation().getYaw() - dxScaled);

        startX = e.getX();
        startY = e.getY();

        Static.mainPanel.repaint();
    }

    public double scaleDifference(int d) {
        return d / Settings.cameraProperties.orbitSensitivity.getValue();
    }
}
