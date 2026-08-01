package com.j3d.engine.interact.input.mouse;

import com.j3d.StaticRefs;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AlwaysMouseOwner extends MouseOwner{
    public AlwaysMouseOwner() {
        super(MOwner.ALWAYS, 2);
    }

    public static final ArrayList<MOwner> ignore = new ArrayList<>(
            List.of(
                    MOwner.TRANSLATE_HANDLE, MOwner.ORBIT, MOwner.ROTATE_HANDLE, MOwner.SCALE_HANDLE
            )
    );

    @Override
    public void mousePressed(MouseEvent e) {
        // we dont gaf about ownership here.
        if (ignore.contains(EngineFrame.getMouseOwner()))
            return;
        super.mousePressed(e);
        // only show if this was a right click.
        if (e.getButton() == MouseEvent.BUTTON3)
            StaticRefs.getMainFrame().showContextMenu(e.getX(), e.getY());
    }
}
