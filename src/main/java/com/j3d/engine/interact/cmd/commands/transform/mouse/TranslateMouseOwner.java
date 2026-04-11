package com.j3d.engine.interact.cmd.commands.transform.mouse;

import com.j3d.engine.interact.cmd.commands.transform.TranslateSelection;
import com.j3d.engine.interact.cmd.commands.transform.handlers.Handle;
import com.j3d.engine.interact.input.mouse.MOwner;

import java.awt.event.MouseEvent;

/**
 * A mouse owner that handles the logic for translating selected objects when a user
 * drags a transformation handle.
 * <p>
 * This class is used exclusively by the {@link TranslateSelection} command.
 * @author Lehlogonolo Poole
 * @see MOwner#TRANSLATE_HANDLE
 * @see TransformMouseOwner
 * @see TranslateSelection
 */
public class TranslateMouseOwner extends TransformMouseOwner {

    public Handle handle;

    public TranslateMouseOwner() {
        super(MOwner.TRANSLATE_HANDLE);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isNotOwner()) return;
        super.mouseReleased(e);
    }
}
