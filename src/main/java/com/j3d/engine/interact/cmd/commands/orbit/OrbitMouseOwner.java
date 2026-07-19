package com.j3d.engine.interact.cmd.commands.orbit;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.StaticConfig;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.generic.CursorManager;
import com.j3d.ui.generic.CursorNames;

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
        boolean locked = StaticConfig.lock;

        int dx = e.getX() - startX;
        int dy = e.getY() - startY;

        double dxScaled = scaleDifference(dx);
        double dyScaled = scaleDifference(dy);

        if (locked) {
            double lockedScale = 2;
            StaticRefs.camera.setPosition(
                    StaticRefs.camera.getPosition().rotateAroundAxis(
                            Vector3.Y(1),
                            dxScaled * lockedScale
                    )
            );
            double dyLockedScale = (StaticRefs.camera.getPosition().getZ() <= 0
                    ? lockedScale : -lockedScale) * 1.4;
            StaticRefs.camera.setPosition(
                    StaticRefs.camera.getPosition().rotateAroundAxis(
                            Vector3.X(1),
                            dyScaled * dyLockedScale
                    )
            );
            StaticRefs.camera.lookAt(Vector3.ZERO);
        } else {

            StaticRefs.camera.getRotation().setPitch(StaticRefs.camera.getRotation().getPitch() - dyScaled);
            StaticRefs.camera.getRotation().setYaw(StaticRefs.camera.getRotation().getYaw() - dxScaled);

        }

        startX = e.getX();
        startY = e.getY();

        StaticRefs.mainPanel.repaint();
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
