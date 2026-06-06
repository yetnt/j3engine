package com.j3d.engine.interact.cmd.commands.orbit;

import com.j3d.Static;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.settings.Settings;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;

import java.awt.event.MouseEvent;

/**
 * A mouse owner that handles camera orbiting logic. When active, dragging the mouse
 * will rotate the scene camera's pitch and yaw.
 * <p>
 * This class is used exclusively by the {@link OrbitCmd} command.
 * @author Lehlogonolo Poole
 * @see MOwner#ORBIT
 * @see MouseOwner
 * @see OrbitCmd
 */
public class OrbitMouseOwner extends MouseOwner {
    private int startX, startY;

    public OrbitMouseOwner() {
        super(MOwner.ORBIT, 0);
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
        super.mouseReleased(e);
        startX = 0;
        startY = 0;
        CursorManager.set(CursorNames.HAND_GRAB);
    }

    @Override
    public void mouseDraggedUsingClickDelay(MouseEvent e) {
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

    /**
     * Scales the raw mouse movement difference by the orbit sensitivity setting.
     * @param d The raw mouse movement difference.
     * @return The scaled movement difference.
     */
    public double scaleDifference(int d) {
        return d / Settings.cameraProperties.orbitSensitivity.getValue();
    }
}
