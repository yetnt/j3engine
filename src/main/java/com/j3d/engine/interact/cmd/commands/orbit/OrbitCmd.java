package com.j3d.engine.interact.cmd.commands.orbit;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.settings.Settings;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.JLabelRichText;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.UUID;

public class OrbitCmd extends Command implements StatefulCommand<Rotation> {
    public static OrbitMouseOwner orbitMouseOwner = new OrbitMouseOwner();
    public UUID orbitCmdUUID = UUID.randomUUID();

    public OrbitCmd() {
        super("orbit", "Orbits the camera around someting");
        this.aliases("o", "rot").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (!CommandsManager.isCurrentStatefulRunning(this)) return;

        run(this, "orbitCmd", Static.camera.getRotation().copy(), logLabel);
    }

    @Override
    public void onStart(Rotation object, SafeJLabel label) {
        orbitMouseOwner.requestOwnership();
        CursorManager.set(CursorNames.HAND_GRAB);
        Static.mainPanel.repaint();
        Static.sceneManager.scheduleOverlap(orbitCmdUUID, c -> label.setText(
                "Use the mouse to orbit the camera around. | "+SafeJLabel.EMPH+": "
                        + SafeJLabel.EMPH + SafeJLabel.EMPH,
                "Sensitivity",
                new JLabelRichText(Settings.cameraProperties.orbitSensitivity.getValue().toString())
                        .font(J3DTheme.TEXT_SECONDARY.color().brighter(), "8"),
                " units per mouse drag"
        ));
        Static.sceneManager.layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().add(orbitCmdUUID, (t2, d) -> {
                    Static.camera.lookAt(t2.getCentroid());
                }));
    }

    public void cleanup(SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        CursorManager.setDefault();
        Static.sceneManager.removeOverlap(orbitCmdUUID);
        label.clear();
        CommandsManager.clearCurrent();
        Static.sceneManager.layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().remove(orbitCmdUUID));
    }

    @Override
    public void onEnter(ActionEvent e, Rotation object, SafeJLabel label) {
        cleanup(label);
        // done
    }

    @Override
    public  void onEsc(ActionEvent e, Rotation object, SafeJLabel label) {
        Static.camera.setRotation((Rotation) object);
        cleanup(label);
    }

}
