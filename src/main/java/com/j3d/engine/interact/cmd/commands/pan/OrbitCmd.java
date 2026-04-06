package com.j3d.engine.interact.cmd.commands.pan;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.ActionEvent;

public class OrbitCmd extends Command implements StatefulCommand<Rotation> {
    public static OrbitMouseOwner orbitMouseOwner = new OrbitMouseOwner();

    public OrbitCmd() {
        super("orbit", "Orbits the camera around someting");
        this.aliases("o", "rot").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        if (!CommandsManager.isCurrentStatefulRunning(this)) return;

        run(this, "orbitCmd", Static.camera.getRotation().copy());
    }

    @Override
    public void onStart(Rotation o) {
        orbitMouseOwner.requestOwnership();
        CursorManager.set(CursorNames.HAND_GRAB);
    }

    @Override
    public void onEnter(ActionEvent e, Rotation o) {
        EngineFrame.setMouseOwner(MOwner.SELECTION);
        CursorManager.setDefault();
        // done
    }

    @Override
    public  void onEsc(ActionEvent e, Rotation o) {
        Static.camera.setRotation((Rotation) o);
        EngineFrame.setMouseOwner(MOwner.SELECTION);
        CursorManager.setDefault();
    }

}
