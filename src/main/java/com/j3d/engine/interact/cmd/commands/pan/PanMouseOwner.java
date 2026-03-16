package com.j3d.engine.interact.cmd.commands.pan;

import com.j3d.Static;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;

import java.awt.event.MouseEvent;

//TODO: A bit jittery. Polish up.
public class PanMouseOwner extends MouseOwner {
    private int startX, startY;
    public PanMouseOwner() {
        super(MOwner.PAN);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isNotOwner()) return;
        startX = e.getX();
        startY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        startX = 0;
        startY = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isNotOwner()) return;

        int dx = e.getX() - startX;
        int dy = e.getY() - startY;

        double dxScaled = scaleDifference(dx);
        double dyScaled = scaleDifference(dy);

        Static.camera.getRotation().setPitch(Static.camera.getRotation().getPitch() - dyScaled);
        Static.camera.getRotation().setYaw(Static.camera.getRotation().getYaw() - dxScaled);

        Static.mainPanel.repaint();
    }

    public double scaleDifference(int d) {
        // 100 pixels = 1 degree
        return d / 100.0;
    }
}
