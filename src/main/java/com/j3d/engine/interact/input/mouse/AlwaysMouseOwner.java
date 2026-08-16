package com.j3d.engine.interact.input.mouse;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.input.MouseClickPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AlwaysMouseOwner extends MouseOwner {

    public static final AlwaysMouseOwner instance = new AlwaysMouseOwner();

    private AlwaysMouseOwner() {
        super(MOwner.ALWAYS, 2);
    }

    public static final ArrayList<MOwner> ignore = new ArrayList<>(
            List.of(
                    MOwner.TRANSLATE_HANDLE, MOwner.ORBIT, MOwner.ROTATE_HANDLE, MOwner.SCALE_HANDLE
            )
    );

    public static AlwaysMouseOwner getSingleInstance() {
        return instance;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // we dont gaf about ownership here.
        broadcast(EventType.MOUSE_CLICKED, new MouseClickPayload(this, e));
        if (ignore.contains(EngineFrame.getMouseOwner()))
            return;
        super.mousePressed(e);
        // only show if this was a right click.
        if (e.getButton() == MouseEvent.BUTTON3)
            StaticRefs.getMainFrame().showContextMenu(e.getX(), e.getY());
    }
}
