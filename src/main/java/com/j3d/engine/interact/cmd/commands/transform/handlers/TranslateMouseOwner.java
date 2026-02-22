package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.engine.interact.input.mouse.MOwner;

import java.awt.event.MouseEvent;

public class TranslateMouseOwner extends TransformMouseOwner {

    public TranslateMouseOwner() {
        super(MOwner.TRANSLATE_HANDLE);
    }
    @Override
    public void mouseDraggedAdapter(HandleType selectedHandle, int dx, int dy, MouseEvent e) throws Exception {

    }
}
